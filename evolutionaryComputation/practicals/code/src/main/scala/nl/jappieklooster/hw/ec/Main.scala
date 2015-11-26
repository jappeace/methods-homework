// Evolutionary Computing Analyzer, it analyzes various genetic algoritms
// Copyright (C) 2015 Jappie Klooster

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/\>.
package nl.jappieklooster.hw.ec

import java.io.{File, PrintWriter}

import org.sameersingh.scalaplot._
import org.slf4j.LoggerFactory

import scala.reflect.io.Path
import scala.util.Random
import OffspringGenerator._
import MemberFactories._
import Fitness._
import scala.util.Properties.lineSeparator

object Main{
	case class Configurations(strLength:Int,popSize:Int,genCount:Int, evolution:Evolution)
	val log = LoggerFactory.getLogger(Main.getClass)
	val random = Random
	val br = lineSeparator
	val folder = "../"
	type GraphData = (String,Seq[Seq[(Double,Double)]])

	val crossMethodsTight = Seq(
		(twoPointCross(random) _, tightlyLinked _, "2X"),
		(twoPointCross(random) _, WithCoinFlipTimesMutation(random,tightlyLinked), "2XM"),
		(uniformCross _, tightlyLinked _, "UX"),
		(uniformCross _, WithCoinFlipTimesMutation(random, tightlyLinked), " UXM")
	)
	def main(args:Array[String]){
		log.info("starting ea")

		
		val crossMethodsRandom = crossMethodsTight.map(a=>{
				val method = a._2 match{
					case WithCoinFlipTimesMutation(_,_) => {
						log.trace(s"${a._3} is flipped")
						WithCoinFlipTimesMutation(random, createRandomlyLinked)
					}
					case _ => {
						log.trace(s"${a._3} is normal")
						createRandomlyLinked
					}
				}
				(a._1, method, a._3)
			}
		)
		val deciptive:Seq[Float] = List(4,0,1,2,3)
		val nonDeciptive:Seq[Float] = List(4,0,0.5f,1,1.5f)
		val expirements =
		Experiment.create("Uniformly scaled", random, uniformlyScaledCountOnes, crossMethodsTight) ++
		Experiment.create("Linearly scaled", random, linearlyScaledCountOnes, crossMethodsTight) ++
		Experiment.create("Block deceptive tight", random, blockValuation(deciptive), crossMethodsTight) ++
		Experiment.create("Block non-deceptive tight", random, blockValuation(nonDeciptive), crossMethodsTight) ++
		Experiment.create("Block deceptive random", random, blockValuation(deciptive), crossMethodsRandom) ++
		Experiment.create("Block non-deceptive random", random, blockValuation(nonDeciptive), crossMethodsRandom) ++
		Experiment.create("Uniformly scaled, tournement elitism", random, uniformlyScaledCountOnes, crossMethodsTight, FittestFilter.tournementElitism) ++
		Experiment.create("Linearly scaled, tournement elitism", random, linearlyScaledCountOnes, crossMethodsTight, FittestFilter.tournementElitism) ++
		Experiment.create("Block deceptive tight, tournement elitism", random, blockValuation(deciptive), crossMethodsTight, FittestFilter.tournementElitism) ++
		Experiment.create("Block non-deceptive tight, tournement elitism", random, blockValuation(nonDeciptive), crossMethodsTight, FittestFilter.tournementElitism) ++
		Experiment.create("Block deceptive random, tournement elitism", random, blockValuation(deciptive), crossMethodsRandom, FittestFilter.tournementElitism) ++
		Experiment.create("Block non-deceptive random, tournement elitism", random, blockValuation(nonDeciptive), crossMethodsRandom, FittestFilter.tournementElitism) ++
		Nil


		val results = expirements.par.map(x => {
			(x, x.bisectionalSearch())
		}).seq
		log.info(s"doing stoastic with ${Experiment.requiredRuns} runs")

		val values = toPlotableStructure(results).toSeq.sortBy(x=>x._1)

		log.info(toLatexWith(asciiGraph)(values))

		write("result.tex", toLatexWith(svgGraph)(values))

		val file = java.io.File.createTempFile("evolutionaryComputing", "dir")
		file.delete()
		file.createNewFile()

		/*
		new PrintWriter(file.getCanonicalFile){
			write(result)
			close()
		}
		*/
	}
	def write(filename:String, content:String) = {
		val outputFile = new File(folder+filename)
		outputFile.delete()
		outputFile.createNewFile()
		new PrintWriter(outputFile){
			write(content)
			close()
		}
	}

	def toPlotableStructure(results:Seq[(Experiment, StoasticRun)]) =
		results.groupBy(x=>x._1.name).map(a=> (a._1, a._2.map(
			// map population size/callcount to points
			xy =>xy._2.byPopSize.map(
				pop=> (pop._1.toDouble,pop._2.count(_.success).toDouble)
			// I think the data points weren't sorted, which is logical considiring it was a map
			).toSeq.sortBy(_._1)
		),
		// the tables with the graphs, just flatten it
		a._2.map(ab => (s"${ab._1.variation}", ab._2))
		))
	import org.sameersingh.scalaplot.Implicits._
	def asciiGraph(data:GraphData) = {
		val weirdAddedChar = 12.toChar.toString
		var result = s"\\begin{verbatim} $br"
		result += output(ASCII, createPlot(data._2)).replaceAll(weirdAddedChar, "")
		result += s"\\end{verbatim} $br"
		result
	}
	def createPlot(xy: Seq[Seq[(Double, Double)]]) = {
		val xAxis = new NumericAxis
		xAxis.label = "population size"
		xAxis.range_= (0.0, Experiment.maxPopSize+Experiment.maxPopSize/Experiment.popUnit)
		val yAxis = new NumericAxis
		yAxis.label = "succeses"
		yAxis.range_= (0.0, Experiment.requiredRuns + Experiment.faultTolerance + 1)

		xyChart(
			data = xy.zipWithIndex.map(
				x=> new MemXYSeries(x._1, crossMethodsTight.apply(x._2)._3)
			),
			x=xAxis,
			y=yAxis,
			showLegend=true,
			legendPosY = LegendPosY.Bottom
		)
	}
	def svgGraph(data:GraphData):String = {
		val name = data._1.replace(' ','_')
		val svgName=name+".svg"
		write(svgName, output(SVG, createPlot(data._2)))
		import sys.process._
		// try to 'compile' the new svg files, should crass gracfully
		s"inkscape -D -z --file=$folder$svgName --export-pdf=$folder$name.pdf --export-latex" !

		// we just assume the 'compiling' worked
		return  s"\\def\\svgwidth{\\columnwidth} $br"+
				s"\\input{$name.pdf_tex}$br"
	}
	def toLatexWith(graphMethod:GraphData => String)
	(values:Iterable[(String, Seq[Seq[(Double, Double)]],Seq[(String,StoasticRun)])]) = values.foldLeft(s"$br\\subsection{Data}")((str,duo) => {
			var result = s"$str $br"
			result += s"\\subsubsection{${duo._1}} $br"
			result += s"\\begin{figure}[ht!] $br"
			result += graphMethod(duo._1, duo._2)
			result += s"\\caption{${duo._1}}"
			result += s"\\end{figure} $br"

			result += stoasticsToLatex(duo._3)
			result += s"$br\\clearpage"
			result
		})


	def createRandomlyLinked = randomlyLinked(random.shuffle(0.to(Experiment.geneLength))) _

	def stoasticsToLatex(runs:Seq[(String, StoasticRun)]) = {
		// did some html in my youth
		val td = "&"
		runs.foldLeft(
			s"$lineSeparator" + // the structure becomes clearer on a new line
			s"\\begin{tabular}{llllll}$lineSeparator" +
			s"Variation $td Success $td Min. population $td Avg. generation "+
			s"$td Avg. fitness call-count $td Avg. time \\\\ \\toprule $lineSeparator"
		)((a,tupple) => {
			val r = tupple._2
			s"$a" +
			s"${tupple._1} $td" +
			s"${r.isSuccesfull} $td ${r.bigestPopcount} $td" +
			s"${r.avergeGeneration} $td ${r.averageFitnessCount} $td" +
			s"${r.bestRunAverageTime} \\\\$lineSeparator"
		}
				) +
		s"\\end{tabular}$lineSeparator"
	}
}

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

	def main(args:Array[String]){
		log.info("starting ea")

		
		val crossMethodsTight = Seq(
			(twoPointCross _, tightlyLinked _, "2X"),
			(uniformCross _, tightlyLinked _, "UX"),
			(uniformCross _, withCoinfliptimesMutation(tightlyLinked, random) _, " UX, Mutation")
		)
		val crossMethodsRandom = Seq(
			(twoPointCross _, createRandomlyLinked, "2X"),
			(uniformCross _, createRandomlyLinked, "UX"),
			(uniformCross _, withCoinfliptimesMutation(createRandomlyLinked, random) _, " UX, Mutation")
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
		Nil


		val results = expirements.par.map(x => {
			(x, x.bisectionalSearch())
		}).seq
		log.info(s"doing stoastic with ${Experiment.requiredRuns} runs")

		val values = toPlotableStructure(results)

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
			data = new XYData(
				new MemXYSeries(xy(0), "2UX"),
				new MemXYSeries(xy(1), "UX"),
				new MemXYSeries(xy(2), "UXM")
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

			result += Experiment.stoasticsToLatex(duo._3)
			result += s"$br\\clearpage"
			result
		})


	def createRandomlyLinked = randomlyLinked(random.shuffle(0.to(Experiment.geneLength))) _
}

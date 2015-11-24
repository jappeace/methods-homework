package nl.jappieklooster.hw.ec

import java.io.{File, PrintWriter}

import org.sameersingh.scalaplot.XYData
import org.slf4j.LoggerFactory

import scala.reflect.io.Path
import scala.util.Random
import OffspringGenerator._
import MemberFactories._
import Fitness._
import util.Properties.lineSeparator

object Main{
	case class Configurations(strLength:Int,popSize:Int,genCount:Int, evolution:Evolution)
	val log = LoggerFactory.getLogger(Main.getClass)
	val random = Random
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

		import org.sameersingh.scalaplot.Implicits._

		val values = toPlotableStructure(results)

		val br = lineSeparator
		val latex = values.foldLeft(s"$br\\subsection{Data}")((str,duo) => {
			var result = s"$str $br"
			result += s"\\subsubsection{${duo._1}} $br"
			result += s"\\begin{figure}[ht!] $br"
			result += s"\\begin{verbatim} $br"

			val xy = duo._2
			val weirdAddedChar = 12.toChar.toString
			result += output(ASCII, xyChart(new XYData(xy(0), xy(1), xy(2)))).replaceAll(weirdAddedChar, "")
			result += s"\\end{verbatim} $br"
			result += s"\\caption{${duo._1}}"
			result += s"\\end{figure}\\clearpage $br"

			result += Experiment.stoasticsToLatex(duo._3)
			result
		})
		log.info(latex)

		val outputFile = new File("result.tex")
		outputFile.delete()
		outputFile.createNewFile()
		new PrintWriter(outputFile){
			write(latex)
			close()
		}

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

	def toPlotableStructure(results:Seq[(Experiment, StoasticRun)]) =
		results.groupBy(x=>x._1.name).map(a=> (a._1, a._2.map(
			// map population size/callcount to points
			xy =>xy._2.byPopSize.map(
				pop=> (pop._1.toDouble,pop._2.length.toDouble)
			// I think the data points weren't sorted, which is logical considiring it was a map
			).toSeq.sortBy(_._1)
		),
		// the tables with the graphs, just flatten it
		a._2.map(ab => (s"${ab._1.variation}", ab._2))
		))
	def createRandomlyLinked = randomlyLinked(random.shuffle(0.to(Experiment.geneLength))) _
}

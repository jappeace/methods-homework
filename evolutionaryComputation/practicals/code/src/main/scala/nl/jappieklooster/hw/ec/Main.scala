package nl.jappieklooster.hw.ec

import java.io.PrintWriter

import org.sameersingh.scalaplot.XYData
import org.slf4j.LoggerFactory

import scala.util.Random
import OffspringGenerator._
import MemberFactories._
import Fitness._

object Main{
	case class Configurations(strLength:Int,popSize:Int,genCount:Int, evolution:Evolution)
	val log = LoggerFactory.getLogger(Main.getClass)
	val random = Random
	def main(args:Array[String]){
		log.info("starting ea")

		
		val crossMethodsTight = Seq(
			(twoPointCross _, tightlyLinked _, " Tight 2x"),
			(uniformCross _, tightlyLinked _, " Tight ux"),
			(uniformCross _, withCoinfliptimesMutation(tightlyLinked, random) _, " Tight ux, R")
		)
		val crossMethodsRandom = Seq(
			(twoPointCross _, createRandomlyLinked, " Random 2x, "),
			(uniformCross _, createRandomlyLinked, " Random ux"),
			(uniformCross _, withCoinfliptimesMutation(createRandomlyLinked, random) _, " Random ux, R")
		)
		val deciptive:Seq[Float] = List(4,0,1,2,3)
		val nonDeciptive:Seq[Float] = List(4,0,0.5f,1,1.5f)
		val expirements =
		Experiment.create("uni scaled", random, uniformlyScaledCountOnes, crossMethodsTight) ++
		Experiment.create("linearly scaled", random, linearlyScaledCountOnes, crossMethodsTight) ++
		//Experiment.create("block decpt", random, blockValuation(deciptive), crossMethodsTight) ++
		//Experiment.create("block non", random, blockValuation(nonDeciptive), crossMethodsTight) ++
		//Experiment.create("block decpt", random, blockValuation(deciptive), crossMethodsRandom) ++
		//Experiment.create("block non", random, blockValuation(nonDeciptive), crossMethodsRandom) ++
		Nil


		val results = expirements.par.map(x => {
			(x, x.bisectionalSearch())
		}).seq
		log.info(s"doing stoastic with ${Experiment.requiredRuns} runs")
		for(tuple <- results){
			import util.Properties.lineSeparator
			val ex = tuple._1
			val re = tuple._2
			log.info(s"${ex.name} (${ex.variation}}):$lineSeparator${re.toTable()}")
		}
		import org.sameersingh.scalaplot.Implicits._

		val values = results.groupBy(x=>x._1.name).map(_._2.map(
			// map population size/callcount to points
			xy => xy._2.byPopSize.map(
				pop=> (pop._1.toDouble,pop._2.length.toDouble)
			// I think the data points weren't sorted, which is logical considiring it was a map
			).toSeq.sortBy(_._1)
		))

		val file = java.io.File.createTempFile("evolutionaryComputing", ""+values.hashCode())
		file.delete()
		file.createNewFile()
		new PrintWriter(file.getCanonicalFile){
			for(exp <- values){
				write(output(SVG, xyChart(new XYData(exp(0), exp(1), exp(2)))))
			}
			close()
		}
	}
	def createRandomlyLinked = randomlyLinked(random.shuffle(0.to(Experiment.geneLength))) _
}

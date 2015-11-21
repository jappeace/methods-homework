package nl.jappieklooster.hw.ec

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
	//		(twoPointCross _, tightlyLinked _, " Tight 2x"),
	//		(uniformCross _, tightlyLinked _, " Tight ux"),
			(uniformCross _, withCoinfliptimesMutation(tightlyLinked, random) _, " Tight ux, R")
		)
		val crossMethodsRandom = Seq(
			(twoPointCross _, createRandomlyLinked, " Random 2x, "),
			(uniformCross _, createRandomlyLinked, " Random ux"),
			(uniformCross _, withCoinfliptimesMutation(createRandomlyLinked, random) _, " Random ux, R")
		)
		val deciptive:Seq[Float] = Seq(4,0,1,2,3)
		val nonDeciptive:Seq[Float] = Seq(4,0,0.5f,1,1.5f)
		val expirements =
		Experiment.create("uni scaled - ", random, uniformlyScaledCountOnes, crossMethodsTight) ++
		//Experiment.create("linearly scaled - ", random, linearlyScaledCountOnes, crossMethodsTight) ++
		//Experiment.create(random, blockValuation(deciptive), crossMethodsTight) ++
		//Experiment.create(random, blockValuation(nonDeciptive), crossMethodsTight) ++
		//Experiment.create(random, blockValuation(deciptive), crossMethodsRandom) ++
		//Experiment.create(random, blockValuatio(nonDeciptive), crossMethodsRandom) ++
		Nil


		val results = expirements.map(ex => new StoasticExperiment(ex)).map(ex => (ex, ex.stoasticEnsurance(2,1)))
		for(tuple <- results){
			import util.Properties.lineSeparator
			log.info(s"${tuple._1.experiment.name}:$lineSeparator${tuple._2.toTable()}")
		}

		import org.sameersingh.scalaplot.Implicits._

		val x = 0.0 until 2.0 * math.Pi by 0.1
		val out = output(ASCII, xyChart(x ->(math.sin(_), math.cos(_))))
	}
	def createRandomlyLinked = randomlyLinked(random.shuffle(0.to(Experiment.geneLength))) _
	def randomGene = s"${random.nextInt(2)}"
}

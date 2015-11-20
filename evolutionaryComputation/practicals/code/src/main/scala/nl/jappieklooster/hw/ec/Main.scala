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

		
		val crossMethodsTight:Seq[(PairedPopulation => Population, String => IMember)] = Seq(
			(twoPointCross _, tightlyLinked _),
			(uniformCross _, tightlyLinked _),
			(uniformCross _, withCoinfliptimesMutation(tightlyLinked, random) _)
		)
		val crossMethodsRandom:Seq[(PairedPopulation => Population, String => IMember)] = Seq(
			(twoPointCross _, createRandomlyLinked),
			(uniformCross _, createRandomlyLinked),
			(uniformCross _, withCoinfliptimesMutation(createRandomlyLinked, random) _)
		)
		val deciptive:Seq[Float] = Seq(4,0,1,2,3)
		val nonDeciptive:Seq[Float] = Seq(4,0,0.5f,1,1.5f)
		val expirements =
		Experiment.create(random, uniformlyScaledCountOnes, crossMethodsTight) ++
		Experiment.create(random, linearlyScaledCountOnes, crossMethodsTight) ++
		Experiment.create(random, blockValuation(deciptive), crossMethodsTight) ++
		Experiment.create(random, blockValuation(nonDeciptive), crossMethodsTight) ++
		Experiment.create(random, blockValuation(deciptive), crossMethodsRandom) ++
		Experiment.create(random, blockValuation(nonDeciptive), crossMethodsRandom)

		val results = expirements.par.flatMap(exp => 0.to(30).par.map(x=> (x,exp.run)))
		for(result <- results){
			log.info(s"result: ${result._1}")
			log.info(s"${result._2}")
		}

		import org.sameersingh.scalaplot.Implicits._

		val x = 0.0 until 2.0 * math.Pi by 0.1
		val out = output(ASCII, xyChart(x ->(math.sin(_), math.cos(_))))
	}
	def createRandomlyLinked = randomlyLinked(random.shuffle(0.to(Experiment.geneLength))) _
	def randomGene = s"${random.nextInt(2)}"
}

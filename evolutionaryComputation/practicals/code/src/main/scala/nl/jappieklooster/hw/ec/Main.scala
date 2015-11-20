package nl.jappieklooster.hw.ec

import org.slf4j.LoggerFactory

import scala.util.Random

object Main{
	case class Configurations(strLength:Int,popSize:Int,genCount:Int, evolution:Evolution)
	val log = LoggerFactory.getLogger(Main.getClass)
	val random = Random
	def main(args:Array[String]){
		log.info("starting ea")

		val stringLength = 20
		val populationSize = 20
		val population = Population.createOneZeros(
			MemberFactories.withCoinfliptimesMutation(
				MemberFactories.simpleMember,
				random
			),
			stringLength,
			populationSize
		)
		val evolution = new Evolution(
			Fitness.uniformlyScaledCountOnes,
			MateSelection.createCompeteWithRandomTournement(random),
			OffspringGenerator.uniformCross,
			FittestFilter.tournementElitism
		)

		val startTime = System.currentTimeMillis()
		val results = evolution.startGenetic(population, 10)
		val runtime = System.currentTimeMillis() - startTime
		for((pop, index) <- results.zipWithIndex){
			println(s"generation $index: $pop")
		}
		log.info(s"time in ms: $runtime")
		import org.sameersingh.scalaplot.Implicits._

		val x = 0.0 until 2.0 * math.Pi by 0.1
		val out = output(ASCII, xyChart(x ->(math.sin(_), math.cos(_))))
	}
	def randomGene = s"${random.nextInt(2)}"
}

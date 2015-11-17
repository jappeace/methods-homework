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
			MemberFactories.globalRandomizedGenes(
				random.shuffle(0.to(stringLength))),
				stringLength,
				populationSize
		)
		val evolution = new Evolution(
			Fitness.uniformlyScaledCountOnes,
			MateSelection.tournamentWinIsParent,
			OffspringGenerator.uniformCross,
			FittestFilter.killParents
		)

		val startTime = System.currentTimeMillis()
		val results = evolution.startGenetic(population, 10)
		val runtime = System.currentTimeMillis() - startTime
		for((pop, index) <- results.zipWithIndex){
			println(s"generation $index: $pop")
		}
		log.info(s"time in ms: $runtime")
	}
	def randomGene = s"${random.nextInt(2)}"
}

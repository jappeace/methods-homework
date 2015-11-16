package nl.jappieklooster.hw.ec

import org.slf4j.LoggerFactory

import scala.util.Random

object Main{
	val log = LoggerFactory.getLogger(Main.getClass)
	val random = Random
	def main(args:Array[String]){
		log.info("starting ea")

		val length = 20
		val population = Population.createOneZeros(
			MemberFactories.globalRandomizedGenes(
				random.shuffle(0.to(length))),
				length,
				40
		)
		val evolution = new Evolution(
			Valuation.uniformlyScaledCountOnes,
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

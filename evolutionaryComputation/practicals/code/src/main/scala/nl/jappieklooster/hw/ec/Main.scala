package nl.jappieklooster.hw.ec

import scala.util.Random

object Main{

	val random = Random
	def main(args:Array[String]){
		println("scala!")

		val length = 20
		val population = Population.createOneZeros(MemberFactories.globalRandomizedGenes(random.shuffle(0.to(length))), length,40)
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
		println(s"time in ms: $runtime")
	}
	def randomGene = s"${random.nextInt(2)}"
}

package nl.jappieklooster.hw.ec

import scala.util.Random

object Main{

	val random = Random
	def main(args:Array[String]){
		println("scala!")
		val population = Population.createOneZeros(20,40)
		val evolution = new Evolution(
			Valuation.uniformlyScaledCountOnes,
			MateSelection.tournamentWinIsParent,
			OffspringGenerator.twoPointCross,
			FittestFilter.killParents
		)

		val startTime = System.currentTimeMillis()
		val results = evolution.startGenetic(population, 10)
		val runtime = System.currentTimeMillis() - startTime
		for(pop <- results){
			println(pop)
		}
		println(s"time in ms: $runtime")
	}
	def randomGene = s"${random.nextInt(2)}"
}

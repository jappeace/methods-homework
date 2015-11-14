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
			OffspringGenerator.uniformCross,
			FittestFilter.killParents
		)

		for(pop <- evolution.startGenetic(population, 10)){
			println(pop)
		}
	}
	def randomGene = s"${random.nextInt(2)}"
}

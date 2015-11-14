package nl.jappieklooster.hw.ec

import scala.util.Random

trait Genetic{
	def getGenes:String
}

/**
 * this object defines how genes will be transfered from parents into children
 */
object OffspringGenerator{
	val random = Random
	def uniformCross(parents:PairedPopulation) : Population = {
		Population(
			parents.members.map(
				fumu => Member(
					// just make key value like structure of the indiviudal genes
					// for comparison
					fumu._1.getGenes.zip(fumu._2.getGenes)
					// where the genes are equal will keep the same, else random
					.map(fm => if (fm._1 == fm._2) fm._1 else Main.randomGene)
					// the map created a char array, fold it back into a string
					.foldLeft("")((x, b) => x + b))
			)
		)
	}
}

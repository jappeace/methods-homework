package nl.jappieklooster.hw.ec

import scala.util.Random
/**
 * This expirement tries to find the optemal number of population for a specific
 * evolution.
 * @param evolution
 */
class Experiment(evolution: Evolution, memberFactory:String => IMember) {
	import Experiment._
	private def createPopulation(size:Int) = {
		Population.createOneZeros(memberFactory, geneLength, size)
	}
	def run():Seq[RunResult]= {
		def findOptimumPopsize(consideringSize:Int, difference:Int): Seq[RunResult] = {
			def expiriment(): Seq[RunResult]={
				val population = createPopulation(consideringSize)

				val startTime = System.currentTimeMillis()
				val evolutionResult = evolution.startGenetic(population, generationCount)
				val runtime = System.currentTimeMillis() - startTime

				// sucesfull if early termination or the last population is good enough
				val success = evolutionResult.length != generationCount || hasGoodEnoughSolution(evolutionResult.last)
				Seq(RunResult(consideringSize, success, runtime))
			}
			if(consideringSize > maxPopSize){
				// we went to deep so we failed
				return Seq(RunResult(consideringSize, false, 0))
			}

			val result = expiriment()
			// 10, 20, 40, 80, 160, 320, 240, 280, 260, 250.
			val hadSuccess = consideringSize != difference*2

			val newDiff = if(hadSuccess) difference/2 else difference*2

			if(newDiff < minPopSize){
				return result
			}
			if(!result.head.success){
				return result ++ findOptimumPopsize(consideringSize+newDiff,newDiff)
			}
			return result ++ findOptimumPopsize(consideringSize-difference/2, difference/2)
		}
		findOptimumPopsize(minPopSize,minPopSize/2)
	}
}
case class RunResult(popSize:Int, success:Boolean, runtime:Long)

object Experiment{

	val generationCount = 10
	val minPopSize = 10
	val maxPopSize = 1280
	val geneLength = 100

	def create(
		random:Random,
		valuationFunction:IHasFitness => Int,
		variationOperators:Seq[(PairedPopulation => Population, String => IMember)]
	):Seq[Experiment] = variationOperators.map(
		variation => new Experiment(new Evolution(
			valuationFunction,
			MateSelection.createCompeteWithRandomTournement(random),
			variation._1,
			FittestFilter.tournementElitism,
			hasGoodEnoughSolution
		),
		variation._2
		)
	)
	def hasGoodEnoughSolution(population: Population):Boolean = {
		// if there exists a member in a population that only contains 1's.
		population.exists(member => !member.getFitness.exists(c => c == '0'))
	}
}
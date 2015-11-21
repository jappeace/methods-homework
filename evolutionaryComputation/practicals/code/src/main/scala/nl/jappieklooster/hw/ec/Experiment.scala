package nl.jappieklooster.hw.ec

import com.itextpdf.text.log.LoggerFactory

import scala.util.Random
/**
 * This expirement tries to find the optemal number of population for a specific
 * evolution.
 * @param evolution
 */
class Experiment(evolution: Evolution, memberFactory:String => IMember) {
	import Experiment._
	val log = LoggerFactory.getLogger(this.getClass)
	private def createPopulation(size:Int) = {
		Population.createOneZeros(memberFactory, geneLength, size)
	}
	def run(consideringSize:Int): RunResult={
		val population = createPopulation(consideringSize)

		val startTime = System.currentTimeMillis()
		val evolutionResult = evolution.startGenetic(population, generationCount)
		val runtime = System.currentTimeMillis() - startTime

		// sucesfull if early termination or the last population is good enough
		val success = hasGoodEnoughSolution(evolutionResult.last)
		RunResult(consideringSize, success, runtime, evolutionResult.length)
	}

	private def findOptimumPopsize(consideringSize:Int, difference:Int): Seq[RunResult] = {
		if(consideringSize > maxPopSize){
			// we went to deep so we failed
			return Seq(RunResult(consideringSize, false, 0,0))
		}

		val result = run(consideringSize)
		// 10, 20, 40, 80, 160, 320, 240, 280, 260, 250.
		val hadSuccess = consideringSize != difference*2

		val newDiff = if(hadSuccess) difference/2 else difference*2

		if(newDiff < popUnit){
			return Seq(result)
		}
		if(!result.success){
			return result ++ findOptimumPopsize(consideringSize+newDiff,newDiff)
		}
		return result ++ findOptimumPopsize(consideringSize-difference/2, difference/2)
	}
	def bisectionalSearch() = findOptimumPopsize(popUnit ,popUnit/2)
}

case class RunResult(popSize:Int, success:Boolean, runtime:Long, generationCount:Int){
	override def toString() :String = {
		s"Run(pop#=$popSize, success=$success, time=$runtime, gen#=$generationCount)"
	}
}

object Experiment{

	val generationCount = 30
	/**
	 * Don't consider pop diferences smaller than this unit
	 */
	val popUnit = 10
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
package nl.jappieklooster.hw.ec

import com.itextpdf.text.log.LoggerFactory

import scala.util.Random
/**
 * This expirement tries to find the optemal number of population for a specific
 * evolution.
 * @param evolution
 */
class Experiment(val name:String, evolution: Evolution, memberFactory:String => IMember) {
	import Experiment._
	val log = LoggerFactory.getLogger(this.getClass)
	private def createPopulation(size:Int) = {
		Population.createOneZeros(memberFactory, geneLength, size)
	}
	def run(consideringSize:Int): RunResult={
		val population = createPopulation(consideringSize)

		val startTime = System.currentTimeMillis()
		val evolutionResult = evolution.startGenetic(population)
		val runtime = System.currentTimeMillis() - startTime

		// sucesfull if early termination or the last population is good enough
		val success = hasGoodEnoughSolution(evolutionResult.last)
		RunResult(consideringSize, success, runtime, evolutionResult.length,evolution.valuation.countCalls())
	}

	def verifyLowest(currentPop:Int, faults:Int, index:Int) : Seq[RunResult] = {
		def failed = Nil
		if(index >= requiredRuns){
			return Nil
		}
		if(faults > faultTolerance){
			return failed
		}
		val exp = run(currentPop)
		val newFaults = if (exp.success) faults else faults+1
		Seq(exp) ++ verifyLowest(currentPop, newFaults,index+1)
	}
	private def findOptimumPopsize(consideringSize:Int, difference:Int): Seq[RunResult] = {
		if(consideringSize > maxPopSize){
			// we went to deep so we failed
			return Nil
		}

		val result = verifyLowest(consideringSize,0,0)
		// 10, 20, 40, 80, 160, 320, 240, 280, 260, 250.
		val hadSuccess = consideringSize != difference*2

		val newDiff = if(hadSuccess) difference/2 else difference*2

		if(newDiff < popUnit){ // steps becoming to small, bort
			return result
		}
		if(result.last.success){
			// if we had success, zoom in
			return result ++ findOptimumPopsize(consideringSize-difference/2, difference/2)
		}
		result ++ findOptimumPopsize(consideringSize+newDiff,newDiff)
	}
	def bisectionalSearch() = StoasticRun(findOptimumPopsize(popUnit ,popUnit/2), requiredRuns)
}

case class RunResult(popSize:Int, success:Boolean, runtime:Long, generationCount:Int, fitnessCallCount:Int){
	override def toString() :String = {
		s"Run(pop#=$popSize, success=$success, time=$runtime, gen#=$generationCount)"
	}
}
import util.Properties.lineSeparator
case class StoasticRun(runs:Seq[RunResult], required:Int) {
	lazy val byPopSize = runs.groupBy(x=>x.popSize)
	lazy val bestRun = byPopSize.get(bigestPopcount).getOrElse(Nil)
	lazy val isSuccesfull = bestRun.size == required
	override def toString(): String = {
		byPopSize.mkString(lineSeparator)
	}
	lazy val bigestPopcount = runs.sortWith((a,b)=>a.popSize > b.popSize).head.popSize
	lazy val avergeGeneration = bestRun.map(x=>x.generationCount).sum/bestRun.length
	lazy val averageFitnessCount = bestRun.map(x=>x.fitnessCallCount).sum/bestRun.length
	lazy val bestRunAverageTime = bestRun.map(x=>x.runtime).sum/bestRun.length

	def toTable():String ={
		s"success:			$isSuccesfull" + lineSeparator +
		s"minimumPop: 		$bigestPopcount	" +lineSeparator+
		s"avgGeneration:		$avergeGeneration" +lineSeparator+
		s"avgFitness:			$averageFitnessCount" +lineSeparator+
		s"avgTime:			$bestRunAverageTime " +lineSeparator
	}
}

object RunResult{
	def createFailed(size: Int) = RunResult(size,false,0,0,0)
}
object Experiment{

	/**
	 * Don't consider pop diferences smaller than this unit
	 */
	val popUnit = 10
	val maxPopSize = 1280
	val geneLength = 100
	val requiredRuns = 30
	val faultTolerance = 1

	def create(
		name:String,
		random:Random,
		valuationFunction:String=>Float,
		variationOperators:Seq[(PairedPopulation => Population, (String=>Float) => String => IMember, String)]
	):Seq[Experiment] = variationOperators.map(
		variation => {
			val probe = Fitness.createProbe(valuationFunction)
		 new Experiment(
			name + variation._3,
			new Evolution(
				probe,
				MateSelection.createCompeteWithRandomTournement(random),
				variation._1,
				FittestFilter.tournementElitism,
				hasGoodEnoughSolution
			),
			variation._2(probe.valuate)
			)
		}
	)
	def hasGoodEnoughSolution(population: Population):Boolean = {
		// if there exists a member in a population that only contains 1's.
		population.exists(member => !member.genes.exists(c => c == '0'))
	}

}
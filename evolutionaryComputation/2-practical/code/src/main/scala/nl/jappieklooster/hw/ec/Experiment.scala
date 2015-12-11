// Evolutionary Computing Analyzer, it analyzes various genetic algoritms
// Copyright (C) 2015 Jappie Klooster

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/\>.
package nl.jappieklooster.hw.ec

import com.itextpdf.text.log.LoggerFactory

import scala.util.Random
/**
 * This expirement tries to find the optemal number of population for a specific
 * evolution.
 * @param evolution
 */
class Experiment(val name:String, val variation:String, evolution: Evolution, memberFactory:String => IMember) {
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
		RunResult(consideringSize, success, runtime, evolutionResult.length,evolution.evaluation.countCalls())
	}

	def verifyLowest(currentPop:Int, faults:Int, index:Int) : Seq[RunResult] = {
		if(index >= requiredRuns){
			return Nil
		}
		if(faults > faultTolerance){
			return Nil
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

		if(newDiff < popUnit){ // steps becoming to small, bort return result
			return result
		}
		if(StochasticRun.isSucces(result)){
			// if we had success, zoom in
			return result ++ findOptimumPopsize(consideringSize-difference/2, difference/2)
		}
		result ++ findOptimumPopsize(consideringSize+newDiff,newDiff)
	}
	def bisectionalSearch() = StochasticRun(findOptimumPopsize(popUnit ,popUnit/2), requiredRuns)
}

case class RunResult(popSize:Int, success:Boolean, runtime:Long, generationCount:Int, fitnessCallCount:Int){
	override def toString() :String = {
		s"Run(pop#=$popSize, success=$success, time=$runtime, gen#=$generationCount)"
	}
}
import util.Properties.lineSeparator
case class StochasticRun(runs:Seq[RunResult], required:Int) {
	lazy val byPopSize = runs.groupBy(x=>x.popSize)
	lazy val bestRun = byPopSize.getOrElse(bigestPopcount, Nil)
	lazy val isSuccesfull = bestRun.size == required
	override def toString(): String = {
		byPopSize.mkString(lineSeparator)
	}
	lazy val bigestPopcount = {
	 	val successes = byPopSize.filter(p =>
			StochasticRun.isSucces(p._2)
		)
		// do the highest so its obvious its a lot
		if(successes.isEmpty) runs.last.popSize else successes.minBy(x=>x._1)._1
	}
	lazy val avergeGeneration = bestRun.map(x=>x.generationCount).sum/bestRun.length
	lazy val averageFitnessCount = bestRun.map(x=>x.fitnessCallCount).sum/bestRun.length
	lazy val bestRunAverageTime = bestRun.map(x=>x.runtime).sum/bestRun.length
}

object StochasticRun{
	def isSucces(results:Seq[RunResult]) =  results.count(r=> r.success) >= (
		Experiment.requiredRuns - Experiment.faultTolerance
	)
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
		variationOperators:Seq[(PairedPopulation => Population, (String=>Float) => String => IMember, String)],
		filter:(Population, Population) => Population = FittestFilter.truncateElitism
	):Seq[Experiment] = variationOperators.map(
		variation => {
			val probe = Evaluation.createProbe(valuationFunction)
		 new Experiment(
			name,
			variation._3,
			new Evolution(
				probe,
				MateSelection.createCompeteWithRandomTournement(random),
				variation._1,
				filter,
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
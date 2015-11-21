package nl.jappieklooster.hw.ec

import Experiment._
class StoasticExperiment(val experiment: Experiment) {

	private def geussLowest={
		// run it four times
		val peekResult = 0.to(3).flatMap(x=>experiment.bisectionalSearch)
		peekResult.foldLeft(RunResult.createFailed(Int.MaxValue))(
			(cur,elem)=> cur match{
				case RunResult(_,false,_,_,_) => elem
				case s if elem.success => if(s.popSize < elem.popSize) s else elem
				case s => s
			}
		)
	}
	def stoasticEnsurance(required:Int, tolerance:Int):StoasticRun ={
		val smallest = geussLowest
		if(!smallest.success){
			return StoasticRun(Seq(smallest), required)
		}
		def verifyLowest(currentPop:Int, faults:Int, index:Int) : Seq[RunResult] = {
			def failed = Seq(RunResult.createFailed(currentPop))
			if(index >= required){
				return Nil
			}
			// to deep gaurd
			if(currentPop > maxPopSize){
				return failed
			}
			if(faults > tolerance){

				val newPopCount = currentPop+popUnit
				return verifyLowest(newPopCount,0,0)
			}
			val exp = experiment.run(currentPop)
			val newFaults = if (exp.success) faults else faults+1
			Seq(exp) ++ verifyLowest(currentPop, newFaults,index+1)
		}
		StoasticRun(verifyLowest(smallest.popSize,0,0),required)
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
		s"minimumPop: 		$bigestPopcount	" +lineSeparator+
		s"avgGeneration:		$avergeGeneration" +lineSeparator+
		s"avgFitness:			$averageFitnessCount" +lineSeparator+
		s"avgTime:			$bestRunAverageTime " +lineSeparator
	}
}

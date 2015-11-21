package nl.jappieklooster.hw.ec

import Experiment._
class StoasticExperiment(experiment: Experiment) {

	private def geussLowest={
		// run it four times
		val peekResult = 0.to(3).flatMap(x=>experiment.bisectionalSearch)
		peekResult.foldLeft(RunResult(Int.MaxValue,false,Int.MaxValue,Int.MinValue))(
			(cur,elem)=> cur match{
				case RunResult(_,false,_) => elem
				case s if elem.success => if(s.popSize < elem.popSize) s else elem
				case s => s
			}
		)
	}
	def stoasticEnsurance(required:Int, tolerance:Int):Seq[RunResult] ={
		val smallest = geussLowest
		if(!smallest.success){
			return Seq(smallest)
		}
		def verifyLowest(currentPop:Int, faults:Int, index:Int) : Seq[RunResult] = {
			if(index >= required){
				return Nil
			}
			// to deep gaurd
			if(currentPop > maxPopSize){
				return Seq(RunResult(currentPop, false, 0,0))
			}
			if(faults > tolerance){
				return verifyLowest(currentPop+popUnit,0,0)
			}
			val exp = experiment.run(currentPop)
			val newFaults = if (!exp.success) faults else faults+1
			Seq(exp) ++ verifyLowest(currentPop, newFaults,index+1)
		}
		verifyLowest(smallest.popSize,0,0)
	}
}

case class StoasticRun(runs:Seq[RunResult]) extends EmulateSeq[RunResult]{
	override def baseSeq: Seq[RunResult] = runs
}
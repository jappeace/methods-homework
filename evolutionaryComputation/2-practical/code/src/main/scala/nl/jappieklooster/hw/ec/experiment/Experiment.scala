package nl.jappieklooster.hw.ec.experiment

import scala.collection.GenSeq

class Experiment[R](code: => R) {
	val totalTimer = Timer()
	def run:RunResult[R] = {
		val timer = Timer()
		val result = timer.time(code)
		RunResult(result, timer.measurement.getOrElse(Moment(-1)))
	}

	def run(times:Int) = totalTimer.time{
		1.to(times).map(x=>run)
	}
	def runPar(times:Int) = totalTimer.time{
		1.to(times).par.map(x=>run)
	}

}
case class RunResult[R](result:R, duration: Moment) {}

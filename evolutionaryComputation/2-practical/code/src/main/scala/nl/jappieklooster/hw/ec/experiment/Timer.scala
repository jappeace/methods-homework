package nl.jappieklooster.hw.ec.experiment

object Timer {
	def measure[R](block: => R): Computation[R] = {
		val t0 = System.nanoTime()
		val result = block    // call-by-name
		val t1 = System.nanoTime()
		Computation(result, t1-t0)
	}
}
case class Computation[R](result:R, duration:Long){
	lazy val seconds:Double = duration / 1000000000.0
}
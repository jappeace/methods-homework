package nl.jappieklooster.hw.ec.experiment

class Timer {
	var last:Long = 0
	def time[R](block: => R): R = {
		val t0 = System.nanoTime()
		val result = block    // call-by-name
		val t1 = System.nanoTime()
		last = t1 - t0
		result
	}
	def seconds:Double = last / 1000000000.0
}
object Timer{
	def apply() = new Timer()
}

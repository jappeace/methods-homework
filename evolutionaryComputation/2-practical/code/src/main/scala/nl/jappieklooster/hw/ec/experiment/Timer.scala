package nl.jappieklooster.hw.ec.experiment

class Timer {
	var measurement:Option[Moment] = None
	def time[R](block: => R): R = {
		val t0 = System.nanoTime()
		val result = block    // call-by-name
		val t1 = System.nanoTime()
		measurement = Some(Moment(t1 - t0))
		result
	}
}
object Timer{
	def apply() = new Timer()
}
case class Moment(duration:Long){
	lazy val seconds:Double = duration / 1000000000.0
}
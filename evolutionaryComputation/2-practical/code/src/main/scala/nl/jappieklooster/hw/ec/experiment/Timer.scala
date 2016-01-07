package nl.jappieklooster.hw.ec.experiment

class Timer {
	val creationTime = System.currentTimeMillis()
	def timeSinceCreation:Float = (System.currentTimeMillis() - creationTime)/1000
}
object Timer{
	def apply() = new Timer()
}

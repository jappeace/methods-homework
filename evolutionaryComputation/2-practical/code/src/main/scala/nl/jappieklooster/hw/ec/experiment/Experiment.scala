package nl.jappieklooster.hw.ec.experiment

import scala.collection.GenSeq

class Experiment[R](code: => R) {

	def run(synchronization:(Int)=>GenSeq[Int])(times:Int) = synchronization(times).map(x=>Timer.measure(code)).toVector.toSeq
}
object Experiment{
	def Parralel(times:Int) = Serial(times).par
	def Serial(times:Int) = 1.to(times)
}

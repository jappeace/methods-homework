package nl.jappieklooster.hw.ec

import scala.util.Random

case class Population(members:Seq[Member]) {
}
object Population{
	val random = Random
	def createOneZeros(length:Int, count:Int):Population =  Population(
		0.to(count).map(x => Member(
			// it says, from 1 to length concat 1 or 0.
			0.to(length).aggregate("")((b, x) => b + s"${random.nextInt(2)}", (a, b) => a + b)
		))
	)
}

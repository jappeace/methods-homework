package nl.jappieklooster.hw.ec

case class Population(members:Seq[Member]) {
}
object Population{
	def createOneZeros(length:Int, count:Int):Population =  Population(
		0.to(count).map(x => Member(
			0.to(length).aggregate("")((b, x) => b + s"$x-", (a, b) => a + "z" + b)
		))
	)
}

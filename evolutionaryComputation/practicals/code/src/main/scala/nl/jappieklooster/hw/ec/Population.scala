package nl.jappieklooster.hw.ec

import scala.util.Random

case class Population(members:Seq[IMember], memberFactory:String => IMember) {
}
object Population{
	def createOneZeros(
			memberFactory:String => IMember,
			length:Int,
			count:Int):Population =  Population(
		0.to(count).map(
			x => memberFactory(
				// it says, from 1 to length concat 1 or 0.
				0.to(length).foldLeft(""){(b, x) => b + Main.randomGene}
			)
		),
		memberFactory
	)
}

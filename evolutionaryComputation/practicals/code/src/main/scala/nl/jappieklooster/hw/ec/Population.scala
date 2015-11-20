package nl.jappieklooster.hw.ec

import scala.collection.{SeqLike, GenSeq}
import scala.collection.parallel.ParSeq
import scala.util.Random

case class Population(members:ParSeq[IMember], memberFactory:String => IMember) extends Seq[IMember] {
	override def length: Int = members.length
	override def iterator: Iterator[IMember] = members.iterator
	override def apply(indx:Int):IMember = members.apply(indx)
}
object Population{
	def createOneZeros(
			memberFactory:String => IMember,
			length:Int,
			count:Int):Population =  Population(
		0.to(count).par.map(
			x => memberFactory(
				// it says, from 1 to length concat 1 or 0.
				0.to(length).foldLeft(""){(b, x) => b + Main.randomGene}
			)
		),
		memberFactory
	)
}

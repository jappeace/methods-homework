package nl.jappieklooster.hw.ec

import scala.collection.{SeqLike, GenSeq}
import scala.collection.parallel.ParSeq
import scala.util.Random

case class Population(members:Seq[IMember], memberFactory:String => IMember) extends EmulateSeq[IMember] {
	override def baseSeq = members
}
object Population{
	def createOneZeros(
			memberFactory:String => IMember,
			geneLength:Int,
			popCount:Int):Population =  Population(
		0.to(popCount-1).map(
			x => memberFactory(
				// it says, from 1 to length concat 1 or 0.
				0.to(geneLength).foldLeft(""){(b, x) => b + Main.randomGene}
			)
		),
		memberFactory
	)
}

package nl.jappieklooster.hw.ec

import scala.collection.{SeqLike, GenSeq}
import scala.collection.parallel.{SeqSplitter, ParSeq}
import scala.util.Random

case class Population(members:Seq[IMember], memberFactory:String => IMember) extends EmulateSeq[IMember]{
	override def baseSeq: Seq[IMember] = members

}
object Population{
	def createOneZeros(
			memberFactory:String => IMember,
			geneLength:Int,
			popCount:Int):Population =  Population(
		0.to(popCount-1).par.map(
			x => memberFactory(
				// it says, from 1 to length concat 1 or 0.
				0.to(geneLength).foldLeft(""){(b, x) => b + OffspringGenerator.randomGene}
			)
		).seq,
		memberFactory
	)
}

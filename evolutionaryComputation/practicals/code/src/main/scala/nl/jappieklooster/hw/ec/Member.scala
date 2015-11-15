package nl.jappieklooster.hw.ec

import scala.util.Random

trait IMember extends Valuable with Genetic
/** a member of the population */
case class SinglyMember(genes:String) extends IMember{
	override def getValuation = genes
	override def getGenes = genes
}
case class ValGeneDecoupledMember(valuation:String, genes:String) extends IMember{
	override def getValuation = valuation
	override def getGenes = genes
}

object MemberFactories{
	def simpleMember(str:String):IMember = SinglyMember(str)
	def globalRandomizedGenes(instructions:TraversableOnce[Int])(str:String):IMember = {
		val genes = instructions.map(inx => str.charAt(inx)).mkString
		ValGeneDecoupledMember(str,genes)
	}
}

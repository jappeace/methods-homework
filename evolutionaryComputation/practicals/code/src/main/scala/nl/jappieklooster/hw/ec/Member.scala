package nl.jappieklooster.hw.ec

/** a member of the population */
case class Member(genes:String) extends Valuable with Genetic{
	override def getValuation = genes
	override def getGenes = genes
}

package nl.jappieklooster.hw.ec

/** a member of the population */
case class Member(genes:String) extends Valuable{
	override def getGenes = genes
}
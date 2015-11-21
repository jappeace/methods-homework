package nl.jappieklooster.hw.ec

import org.slf4j.LoggerFactory

import scala.util.Random

trait IMember extends IHasFitness with Genetic
/** a member of the population */
case class SinglyMember(genes:String) extends IMember{
	override def getFitness = genes
	override def getGenes = genes
}
case class ValGeneDecoupledMember(valuation:String, genes:String) extends IMember{
	override def getFitness = valuation
	override def getGenes = genes
}

object MemberFactories{
	def tightlyLinked(str:String):IMember = SinglyMember(str)
	def randomlyLinked(instructions:TraversableOnce[Int])(str:String):IMember = {
		val genes = instructions.map(inx => str.charAt(inx)).mkString
		ValGeneDecoupledMember(str,genes)
	}
	val log = LoggerFactory.getLogger(this.getClass)
	def withCoinfliptimesMutation(creationFunc:String => IMember, random:Random)(str:String):IMember = {
		def countFlips(integer:Integer):Integer = {
			if(random.nextBoolean()){
				return countFlips(integer+1)
			}
			integer
		}
		val flipCount = countFlips(-1)
		val mutated = 0.to(flipCount).foldLeft(str)((str, modficationCount) => {
			def decideIndex(prev:Int):Int = if((prev == 0) || (prev == str.length))
				decideIndex(random.nextInt(str.length)) else prev
			val index = decideIndex(0)

			val begin = str.take(index-1)
			val mutation = if (str.charAt(index) == '0') '1' else '0'
			val end = str.drop(index)
			begin + mutation + end
		})
		creationFunc(mutated)
	}
}

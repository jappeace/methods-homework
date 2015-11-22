package nl.jappieklooster.hw.ec

import org.slf4j.LoggerFactory

import scala.util.Random

trait IMember extends IHasFitness with Genetic
case class Member(valuation:Float, gen:String) extends IMember{
	override def fitness = valuation
	override def genes = gen
}

object MemberFactories{
	def tightlyLinked(func:String=>Float)(str:String):IMember = Member(func(str), str)
	def randomlyLinked(instructions:TraversableOnce[Int])(func:String=>Float)(str:String):IMember = {
		val genes = instructions.map(inx => str.charAt(inx)).mkString
		Member(func(str), str)
	}
	val log = LoggerFactory.getLogger(this.getClass)
	def withCoinfliptimesMutation(creationFunc:(String=>Float) => String => IMember, random:Random)(func:String=>Float)(str:String):IMember = {
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
		creationFunc(func)(mutated)
	}
}

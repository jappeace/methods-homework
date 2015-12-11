// Evolutionary Computing Analyzer, it analyzes various genetic algoritms
// Copyright (C) 2015 Jappie Klooster

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/\>.
package nl.jappieklooster.hw.ec.model

import org.slf4j.LoggerFactory

import scala.util.Random
import nl.jappieklooster.hw.ec.algorithm.IHasFitness

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
	case class WithCoinFlipTimesMutation(random:Random, creationFunc:(String=>Float) => String => IMember) extends ((String=>Float)=>(String) => IMember){
		def apply(func:String=>Float) = (str:String) => {
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
		def countFlips(integer:Integer):Integer = {
			if(random.nextBoolean()){
				return countFlips(integer+1)
			}
			integer
		}
	}
}

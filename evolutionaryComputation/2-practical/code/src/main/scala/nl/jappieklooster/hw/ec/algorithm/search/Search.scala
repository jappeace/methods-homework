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
package nl.jappieklooster.hw.ec.algorithm.search

import com.itextpdf.text.log.LoggerFactory
import nl.jappieklooster.hw.ec.Main
import nl.jappieklooster.hw.ec.algorithm.search.Search._
import nl.jappieklooster.hw.ec.model.{Graph, IMember, Vertex}

import scala.annotation.tailrec
import scala.util.Random

/**
 * Search, it depends on how you configure it if its global or local.
 * it executes the search method, then checks if its happy, if not it'll
 * try again, if so the decideResult is called
 * @param method
 * @param stopCondition
 * @param decideResult
 */
class Search(
		method:SearchMethod,
		stopCondition:StopCondition = StopCondition.onEqual,
		decideResult:ResultDecision = ResultDecision.current
	){
	@tailrec
	final def search(previous: IMember):IMember = {
		val result = method(previous)
		if(stopCondition(result,previous)){
			return decideResult(result, previous)
		}else{
			return search(result)
		}
	}
}
object Search {
	type SearchMethod = IMember => IMember
	type StopCondition = (IMember, IMember)=>Boolean
	object StopCondition{
		def onEqual(a:IMember, b:IMember):Boolean = a.fitness == b.fitness
		def isWorse(a:IMember, b:IMember):Boolean = a.fitness < b.fitness
		def isWorseEq(a:IMember, b:IMember):Boolean = a.fitness <= b.fitness
		def resetRetryOnBetter(which:RetryOnResult, stopCondition: StopCondition)(current: IMember, previous: IMember): Boolean = {
			val result = stopCondition(current,previous)
			if(!result){
				which.reset
			}
			result
		}
	}
	type ResultDecision = (IMember, IMember) => IMember
	object ResultDecision{
		def current(cur:IMember, prev:IMember) = cur
		def previous(cur:IMember, prev:IMember) = prev
	}

	object Iterative{
		def swapMutation(times:Int, random:Random, memberFactory:String => IMember)(member:IMember):IMember ={
			val gen = member.genes
			def randomGenePoint = random.nextInt(gen.length)
			def create(prev:String, x:Int):String = {
				val one = randomGenePoint
				val two = randomGenePoint
				if(prev(one)== prev(two)){
					create(prev,x)
				}else{
					swapChar(prev)(one, two)
				}
			}

			return memberFactory(1.to(times).foldLeft(gen)(create))
		}
	}
	/**
	* Swap characters at specified positions.
	*/
	def swapChar(partitioning:String)(one:Int, two:Int):String = { // how is this not a standard function?!
		val builder = new StringBuilder(partitioning)
		builder.setCharAt(one, partitioning(two))
		builder.setCharAt(two, partitioning(one))
		builder.mkString
	}

	class Probe extends SearchMethod{
		var tracked = List[IMember]()
		override def apply(member: IMember): IMember = {
			tracked = tracked :+ member
			member
		}
	}
	val log = Main.log
	class RetryOnResult(repitions:Int, var method: SearchMethod) extends ResultDecision{
		val resetvalue = 0
		private var current = resetvalue
		override def apply(result: IMember, previous: IMember): IMember = {
			current += 1

			if(current > repitions*0.5 && current%(repitions/10)== 0){
				log.info(s"current retry = $current")
			}
			if(current < repitions){
				method(previous)
			}
			previous
		}
		def reset = current = resetvalue
	}
}

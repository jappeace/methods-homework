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
package nl.jappieklooster.hw.ec.algorithm

trait IHasFitness{
	def fitness:Float
}
trait FitnessEvaluator{
	private var calls = 0
	protected def getFunction:String => Float
	def valuate(x:String):Float ={
		calls += 1
		getFunction(x)
	}
	def countCalls():Int = {
		val result = calls
		calls = 0
		result
	}
}

object Evaluation{

	private def whereCharIsOne(c:Char) = c == '1'
	def uniformlyScaledCountOnes(s:String):Float = s.count(whereCharIsOne)

	def linearlyScaledCountOnes(s:String):Float = s.
			// make a map like thing of it where the index is the key
			zipWithIndex.
			// select the elements where 1
			filter(_._1 == '1').
			// throw away the elements and keep the indeci
			// also increase the index value by one
			map(_._2+1).
			// result
			sum
	def blockValuation(block:Seq[Float])(s:String):Float = {
		val size = block.size-1
		// make blocks of the string
		s.grouped(size).map(
			// get the value specified in the block
			str => block(size - uniformlyScaledCountOnes(str).toInt)
		).sum
	}
	def createProbe(f:String=>Float):FitnessEvaluator = new FitnessEvaluator {
		protected override def getFunction: (String) => Float = f
	}
}

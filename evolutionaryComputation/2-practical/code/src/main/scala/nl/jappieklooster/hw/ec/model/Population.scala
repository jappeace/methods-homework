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

import scala.util.Random

case class Population(members:Seq[IMember], memberFactory:String => IMember) extends EmulateSeq[IMember] {
	override def baseSeq: Seq[IMember] = members

}
object Population{
	def createOneZeros(
			memberFactory:String => IMember,
			geneLength:Int,
			popCount:Int):Population =  Population(
		0.to(popCount-1).map(
			x => memberFactory(
				// it says, from 1 to length concat 1 or 0.
				0.to(geneLength).foldLeft(""){(b, x) => b + OffspringGenerator.randomGene}
			)
		),
		memberFactory
	)

	def flipBit(c:Char):Char = if (c == '0') '1' else '0'
	/**
	 * return the original and flip every other bit, this basically makes an
	 * equal amount of ones and zeros.
	 * @param input
	 * @return
	 */
	def inverseMirror(input:String):String ={
		input + input.map(flipBit)
	}
	def createEqualOnesZeros(
		random:Random,
		memberFactory:String => IMember,
		geneLength:Int,
		popCount:Int):Population =  {
		if(geneLength % 2 == 1){
			throw new Exception("Doesn't work with odd genelength")
		}
		val initial = createOneZeros((s) => Member(0,s), geneLength/2, popCount)
		Population(
			initial.map(x=> memberFactory(
				// we need to shuffle the inverse mirror, because otherwise
				// it'll lose some entropy (the posibility space becomes less
				// without shuffeling because the first part is only randomly
				// generated, the second part is its mirror image)
				random.shuffle(
					inverseMirror(x.genes).toCharArray.toSeq
				).mkString
			)
		),
		memberFactory
		)
	}
}

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

import nl.jappieklooster.hw.ec.algorithm.OffspringGenerator

import scala.util.Random

case class Population(members:Seq[IMember], memberFactory:String => IMember) extends EmulateSeq[IMember] {
	override def baseSeq: Seq[IMember] = members

}
object Population{
	type SeedMethod = Int=>String
	def createWithMethod(
			memberFactory:String => IMember,
			geneLength:Int,
			popCount:Int,
			seedMethod:SeedMethod
			):Population = Population(
		1.to(popCount).map(
			x => memberFactory(
				// it says, from 1 to length concat 1 or 0.
				seedMethod(geneLength)
			)
		),
		memberFactory
	)
	def createOneZeros(
			memberFactory:String => IMember,
			geneLength:Int,
			popCount:Int):Population = createWithMethod(
		memberFactory,
		geneLength,
		popCount,
		OffspringGenerator.randomGenes
	)

	def createEqualOnesZeros(
		memberFactory:String => IMember,
		geneLength:Int,
		popCount:Int):Population = createWithMethod(
		memberFactory,
		geneLength,
		popCount,
		OffspringGenerator.randomBalancedGenes
	)
}

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

case class Population(members:Seq[IMember], memberFactory:String => IMember) extends EmulateSeq[IMember]{
	override def baseSeq: Seq[IMember] = members

}
object Population{
	def createOneZeros(
			memberFactory:String => IMember,
			geneLength:Int,
			popCount:Int):Population =  Population(
		0.to(popCount-1).par.map(
			x => memberFactory(
				// it says, from 1 to length concat 1 or 0.
				0.to(geneLength).foldLeft(""){(b, x) => b + OffspringGenerator.randomGene}
			)
		).seq,
		memberFactory
	)
}

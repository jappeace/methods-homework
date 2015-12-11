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

case class Population[T <: Seq[IMember]](members:Seq[T], memberFactory:String => T) extends EmulateSeq[T]{
	override def baseSeq: Seq[T] = members
	def pairedToPop(
			how:(IndexedSeq[(Char, Char)]) => IndexedSeq[Any]
			):Population =  {
		Population[T](
			members.flatMap(
				fumu => {

					val genesF = fumu.head.genes
					val genesM = fumu.last.genes
					def createGenes = how(
						// just make key value like structure of the indiviudal genes
						// for comparison
						genesF.zip(genesM)
						// mkstring uses a builder to avoid ridiculous concatination problems
					).mkString
					def createIfNew(child:String):IMember ={
						if(child == genesF){
							return fumu.head
						}
						if (child == genesM) {
							return fumu.last
						}
						memberFactory(child)
					}

					// we need two children so we return a list, flatmap will
					// flatten that list again later
					List(createIfNew(createGenes), createIfNew(createGenes))
				}
			),
			memberFactory
		)
	}
}
object Population{
	def createOneZeros[T](
			memberFactory:String => T,
			geneLength:Int,
			popCount:Int
	):Population[T] =  Population(
		1.to(popCount).par.map(
			x => memberFactory(
				// it says, from 1 to length concat 1 or 0.
				1.to(geneLength).foldLeft(""){(b, x) => b + OffspringGenerator.randomGene}
			)
		).seq,
		memberFactory
	)
}

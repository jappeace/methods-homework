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

import com.itextpdf.text.log.LoggerFactory

import scala.util.Random

trait Genetic{
	def genes:String
}
case class Pair(father:IMember, mother:IMember)
case class PairedPopulation(members:Seq[Pair], memberFactory:String => IMember) extends EmulateSeq[Pair]{
	override def baseSeq = members
	/**
	 * The surrounding function, it will map two members of a population
	 * to one to create a new population, it will be done by sending the
	 * zipped genes to the how function which will figure out the details
	 * @param how
	 * @return
	 */
	def pairedToPop(
		how:(IndexedSeq[(Char, Char)]) => IndexedSeq[Any]
	):Population =  {
		Population(
			members.flatMap(
				fumu => {
					val genesF = fumu.father.genes
					val genesM = fumu.mother.genes
					def createGenes = how(
						// just make key value like structure of the indiviudal genes
						// for comparison
						genesF.zip(genesM)
					// mkstring uses a builder to avoid ridiculous concatination problems
					).mkString
					def createIfNew(child:String):IMember ={

						if(child == genesF){
							return fumu.father
						}
						if (child == genesM) {
							return fumu.mother
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


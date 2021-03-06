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
package nl.jappieklooster.hw.ec

import com.itextpdf.text.log.LoggerFactory

import scala.util.Random

trait Genetic{
	def genes:String
}
case class Pair(father:IMember, mother:IMember)
case class PairedPopulation(members:Seq[Pair], memberFactory:String => IMember) extends EmulateSeq[Pair]{
	override def baseSeq = members
	/**
	 * The surounding function, it will map two members of a population
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
						import OffspringGenerator._
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
object OffspringGenerator{
	def uniformCross(x:PairedPopulation) : Population = x.pairedToPop(
		/* kindoff a weird implementation, but it works because
		 1==1V0==0, then the selection is irrelevant
		 but iff 1==0 or 0==1, then the randomGene will just
		 create a 1 or 0 without looking at the parents.
		*/
		genes =>
			// where the genes are equal will keep the same, else random
			genes.map(fm => if (fm._1 == fm._2) fm._1 else randomGene)
	)
	def singlePointCross(rand:Random)(x:PairedPopulation) : Population = x.pairedToPop(
		genes => {
			val point = rand.nextInt(genes.length)
			// where the genes are equal will keep the same, else random
			genes.zipWithIndex.map(fm => {
				if (point < fm._2) fm._1._1 else fm._1._2
			})
		}
	)
	val log = LoggerFactory.getLogger("offspringernerator")
	def inverResult(selected:Char, one:Char, two:Char) = if(selected == one) two else one
	def keepSelected(selected:Char, one:Char, two:Char) = selected
	def twoPointCross(onFirstBig:(Char,Char,Char)=>Char, genes:IndexedSeq[(Char,Char)], first:Int, second:Int):IndexedSeq[Char] = {
		val isFirstSmall = first < second
		val result = genes.zipWithIndex.map(fm => {
			def select(start:Int, end:Int):Char = {
				if(fm._2 < start) fm._1._1 else{
					if(fm._2 < end){
						fm._1._2
					}else fm._1._1
				}
			}
			if(isFirstSmall){
				select(first,second)
			}else{
				onFirstBig(select(second,first),fm._1._1,fm._1._2)
			}
		})
		result
	}
	def twoPointCross(rand:Random, onFirstBig:(Char,Char,Char)=>Char)(x:PairedPopulation) : Population = x.pairedToPop(
		genes => {
			val first = rand.nextInt(genes.length)
			val second = rand.nextInt(genes.length)
			log.debug(s"crossing with $first and $second")
			// where the genes are equal will keep the same, else random
			twoPointCross(onFirstBig, genes,first,second)
		}
	)
	val random = Random
	def randomGene = s"${random.nextInt(2)}"
}

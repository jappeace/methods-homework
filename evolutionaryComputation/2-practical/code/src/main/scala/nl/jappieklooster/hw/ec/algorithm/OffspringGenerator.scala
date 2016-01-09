package nl.jappieklooster.hw.ec.algorithm

import com.itextpdf.text.log.LoggerFactory
import nl.jappieklooster.hw.ec.model.{PairedPopulation, Population}

import scala.util.Random

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

	def balancedUniformCross(x:PairedPopulation) :Population = x.pairedToPop(genes =>{
		val hammingDistance = genes.count(a => a._1 != a._2)

		// if the hamming distance is to big, crossover will have little effect,
		// so inverting one of the parents will result in the same score but a
		// closer hamming distance
		val hammingClosed = (if(hammingDistance > genes.length/2){
			genes.map(a => (flipBit(a._1), a._2))
		}else{
			genes
		}).zipWithIndex

		// filter is a little confusing, it selects the predicate that matches
		val notMatching = hammingClosed.filter(cur => cur._1._1 != cur._1._2)

		// Can there be an uneven amount of notMatching bits in two balanced solutions?
		// No, this is impossible. So we just let the program crash if this occurs
		val newGenesInstructions = Map(randomBalancedGenes(notMatching.length).zipWithIndex.map(
			pair => notMatching(pair._2)._2 -> pair._1
		):_*)

		hammingClosed.map(fm => if(fm._1._1 == fm._1._2) fm._1._1 else newGenesInstructions(fm._2) )
	})
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
	def inverseResult(selected:Char, one:Char, two:Char) = if(selected == one) two else one
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
	def randomGenes(geneLength:Int) = 1.to(geneLength).foldLeft(""){(b, x) => b + OffspringGenerator.randomGene}
	def randomBalancedGenes(geneLength:Int):String = {
		if(geneLength % 2 == 1){
			throw new Exception("Doesn't work with odd genelength")
		}
		val initial = randomGenes(geneLength/2)
		random.shuffle(
			inverseMirror(initial).toCharArray.toSeq
		).mkString
	}
	def flipBit(c:Char):Char = inverseResult(c,'1','0')
	/**
	 * return the original and flip every other bit, this basically makes an
	 * equal amount of ones and zeros.
	 * @param input
	 * @return
	 */
	def inverseMirror(input:String):String ={
		input + input.map(flipBit)
	}
}

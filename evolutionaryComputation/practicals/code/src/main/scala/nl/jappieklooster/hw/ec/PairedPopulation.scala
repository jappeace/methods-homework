package nl.jappieklooster.hw.ec

trait Genetic{
	def getGenes:String
}
case class Pair(father:Genetic, mother:Genetic)
case class PairedPopulation(members:Seq[Pair], memberFactory:String => IMember){
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
			members.map(
				fumu => {
					val child = how(
						fumu.father.getGenes.zip(fumu.mother.getGenes)
					)
					// just make key value like structure of the indiviudal genes
					// for comparison
					memberFactory(child.mkString)
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
			genes.map(fm => if (fm._1 == fm._2) fm._1 else Main.randomGene)
	)
	def singlePointCross(x:PairedPopulation) : Population = x.pairedToPop(
		genes => {
			val point = Main.random.nextInt(genes.length)
			// where the genes are equal will keep the same, else random
			genes.zipWithIndex.map(fm => {
				if (point < fm._2) fm._1._1 else fm._1._2
			})
		}
	)
	def twoPointCross(x:PairedPopulation) : Population = x.pairedToPop(
		genes => {
			val end = Main.random.nextInt(genes.length)
			val start = Main.random.nextInt(genes.length - end)
			// where the genes are equal will keep the same, else random
			genes.zipWithIndex.map(fm => {
				if(fm._2 < start) fm._1._1 else{
					if(fm._2 < end){
						fm._1._2
					}else fm._1._1
				}
			})
		}
	)
}

package nl.jappieklooster.hw.ec

class Evolution(
	val valuation: FitnessEvaluator,
	mateSelectorFactory:(IHasFitness => Int)=>IMateSelector,
	offSpringGenerator: PairedPopulation => Population,
	/**
	 * fittest filter, (valuation, parents, children) => fittestpop
	 */
	fittestFilter: (IHasFitness => Int, Population, Population) => Population,
	hasGoodEnoughSolution: Population => Boolean
) {
	val mateSelector = mateSelectorFactory(valuation.valuate)
	private def step(parents:Population):Population = fittestFilter(
			valuation.valuate,
			parents,
			offSpringGenerator(
				mateSelector.selectFrom(
					parents
				)
			)
		)
	def startGenetic(seed:Population): Seq[Population]= genetic(
		Seq(seed)
	)
	private def genetic(prev:Seq[Population]): Seq[Population]={
		val parents = prev.last
		if(hasGoodEnoughSolution(parents)){
			return prev
		}
		val children = step(parents)
		if(children == parents){
			return prev
		}
		genetic(prev :+ children)
	}
}


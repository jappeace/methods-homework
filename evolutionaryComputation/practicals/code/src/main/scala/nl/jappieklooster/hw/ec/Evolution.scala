package nl.jappieklooster.hw.ec

class Evolution(
	val valuation: FitnessEvaluator,
	mateSelector:IMateSelector,
	offSpringGenerator: PairedPopulation => Population,
	/**
	 * fittest filter, (valuation, parents, children) => fittestpop
	 */
	fittestFilter: (Population, Population) => Population,
	hasGoodEnoughSolution: Population => Boolean
) {
	private def step(parents:Population):Population = fittestFilter(
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
		if (children == parents) {
			return prev
			}
		genetic(prev :+ children)
	}
}


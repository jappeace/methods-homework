package nl.jappieklooster.hw.ec

class Evolution(
	valuation: IHasFitness => Int,
	mateSelectorFactory:(IHasFitness => Int)=>IMateSelector,
	offSpringGenerator: PairedPopulation => Population,
	/**
	 * fittest filter, (valuation, parents, children) => fittestpop
	 */
	fittestFilter: (IHasFitness => Int, Population, Population) => Population,
	hasGoodEnoughSolution: Population => Boolean
) {
	val mateSelector = mateSelectorFactory(valuation)
	private def step(parents:Population):Population = fittestFilter(
			valuation,
			parents,
			offSpringGenerator(
				mateSelector.selectFrom(
					parents
				)
			)
		)
	def startGenetic(seed:Population, times:Int): Array[Population]= genetic(
		Array(seed), Math.abs(times)
	)
	private def genetic(prev:Array[Population], times:Int): Array[Population]={
		if (times <= 0) prev else{
			val last = prev.last
			if(hasGoodEnoughSolution(last)){
				return prev
			}
			return genetic(prev :+ step(last),times-1)
		}
	}
}


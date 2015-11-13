package nl.jappieklooster.hw.ec

class Evolution(
	valuation: Valuable => Int,
	mateSelector: (Valuable => Int, Population) => PairedPopulation,
	offSpringGenerator: PairedPopulation => Population,
	/**
	 * fittest filter, (valuation, parents, children) => fittestpop
	 */
	fittestFilter: (Valuable => Int, Population, Population) => Population
) {
	private def step(parents:Population):Population = fittestFilter(
			valuation,
			parents,
			offSpringGenerator(
				mateSelector(
					valuation,
					parents
				)
			)
		)
	def startGenetic(seed:Population, times:Int): Array[Population]= 0.to(times)
		.foldLeft(Array[Population]()){
			case (Array(), i) => Array(seed)
			case(e, i) => e :+ step(e.last)
		}
}
package nl.jappieklooster.hw.ec

object FittestFilter {
	/** drop the parents */
	def killParents(
		parents : Population,
		children : Population
	) : Population = {
		children
	}

	def truncateElitism(
			valuation : IHasFitness => Int,
			parents : Population,
			children : Population
			) : Population = {
		val combined = parents ++ children
		// end up sorting anyways, no idea why you would do tournement selection
		val sorted = combined.sortWith((x, y) => x.fitness > y.fitness)
		parents.copy(members = sorted.take(parents.length))
	}

	def tournementElitism(
		parents : Population,
		children : Population
	): Population={
		val combined = parents.members.zip(children.members)
		Population(
			combined.map(x=> MateSelection.pickBest(x._1, x._2)),
			parents.memberFactory
		)
	}


}

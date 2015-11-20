package nl.jappieklooster.hw.ec

object FittestFilter {
	/** drop the parents */
	def killParents(
		valuation : IHasFitness => Int,
		parents : Population,
		children : Population
	) : Population = {
		children
	}

	/**
	 * Take the best of the children and parents.
	 * @param valuation
	 * @param parents
	 * @param children
	 * @return
	 */
	def truncateElitism(
			valuation : IHasFitness => Int,
			parents : Population,
			children : Population
			) : Population = {
		val combined = parents ++ children
		// end up sorting anyways, no idea why you would do tournement selection
		val sorted = combined.sortWith((x, y) => valuation(x) > valuation(y))
		parents.copy(members = sorted.take(parents.members.length))
	}
	def tournementElitism(
		valuation : IHasFitness => Int,
		parents : Population,
		children : Population
	): Population={
		val tournement = MateSelection.pickBestWith(valuation)_

		val combined = parents.members.zip(children.members)

		Population(
			combined.map(x=> tournement(x._1, x._2)),
			parents.memberFactory
		)
	}


}

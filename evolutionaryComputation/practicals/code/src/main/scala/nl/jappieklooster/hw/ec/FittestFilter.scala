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
	def elitism(
			valuation : IHasFitness => Int,
			parents : Population,
			children : Population
			) : Population = {
		val combined = parents.members ++ children.members
		// end up sorting anyways, no idea why you would do tournement selection
		val sorted = combined.sortWith((x, y) => valuation(x) > valuation(y))
		parents.copy(members = sorted.take(parents.members.length))
	}


}

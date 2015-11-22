package nl.jappieklooster.hw.ec

object FittestFilter {
	/** drop the parents */
	def killParents(
		parents : Population,
		children : Population
	) : Population = {
		children
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

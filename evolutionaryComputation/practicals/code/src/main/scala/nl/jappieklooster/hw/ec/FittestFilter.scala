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

	def tournementElitism(
		valuation : IHasFitness => Int,
		parents : Population,
		children : Population
	): Population={
		val tournement = MateSelection.pickBestWith(valuation)_

		val combined = parents.members.zip(children.members)

		Population(
			combined.par.map(x=> tournement(x._1, x._2)).seq,
			parents.memberFactory
		)
	}


}

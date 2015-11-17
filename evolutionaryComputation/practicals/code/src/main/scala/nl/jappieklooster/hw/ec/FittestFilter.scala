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
}

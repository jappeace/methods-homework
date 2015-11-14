package nl.jappieklooster.hw.ec

object FittestFilter {
	/** drop the parents */
	def killParents(
		valuation : Valuable => Int,
		parents : Population,
		children : Population
	) : Population = {
		children
	}
}

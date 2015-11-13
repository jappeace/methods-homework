package nl.jappieklooster.hw.ec

class Evolution(
	valuation: Valuable => Int,
	mateSelector: (Valuable => Int, Population) => PairedPopulation,
	offSpringGenerator: PairedPopulation => Population
) {
	private def step(start:Population):Population = {
		return start
	}
	def startGenetic(seed:Population, times:Int): Array[Population]={
		0.to(times).map()
	}
}

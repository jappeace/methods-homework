package nl.jappieklooster.hw.ec

object MateSelection {

	def tournamentWinIsParent(
		valuator : Valuable => Int, bachelors: Population
	) : PairedPopulation = {
		val mem = bachelors.members
		return mem
		// first matchup the members into a tournement
		// to fill 1 pair we need 4 contestents (as 2 will lose)
		.zip(
			mem.slice(1, mem.length) :+ mem.head
		).zip(
			mem.reverse
		).zip(
			(mem.slice(1, mem.length) :+ mem.head).reverse
		)
		// because we used reverse half can be dropped
		.drop(Math.floor(mem.length).toInt)

		// flatten the thing to give myself not a headache later
		.map(x => (x._1._1._1, x._1._1._2, x._1._2, x._2))

		// the actuall tournement
		.foldLeft(PairedPopulation(Nil))((mates, contestents) => {
			val mother = if (
				valuator(contestents._1) > valuator(contestents._2)
			) contestents._1 else contestents._2
			val father = if(
				valuator(contestents._3) > valuator(contestents._4)
			) contestents._3 else contestents._4
			return PairedPopulation(mates.members :+ (mother, father))
			}
		)
	}
}

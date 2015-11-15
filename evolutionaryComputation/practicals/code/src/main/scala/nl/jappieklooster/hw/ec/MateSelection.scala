package nl.jappieklooster.hw.ec

object MateSelection {

	def tournamentWinIsParent(
		valuator : Valuable => Int, bachelors: Population
	) : PairedPopulation = {
		def tournement(contender:(Member, Member, Member, Member)):Pair = {
			val mother = if (
				valuator(contender._1) > valuator(contender._2)
			) contender._1 else contender._2
			val father = if(
				valuator(contender._3) > valuator(contender._4)
			) contender._3 else contender._4
			Pair(mother, father)
		}
		val mem = bachelors.members
		val matchup = mem
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
		.drop(Math.floor(mem.length/2).toInt)

		// creating the final structure
		val brothers = matchup.map(x => {
			// flatten the thing to give myself not a headache later
			tournement((x._1._1._1, x._1._1._2, x._1._2, x._2))
		})
		val sisters = matchup.map(x => {
			// flatten the thing to give myself not a headache later
			tournement((x._1._1._1, x._2, x._1._1._2, x._2))
		})

		// the actuall tournement
		return PairedPopulation(sisters ++ brothers)
	}
}

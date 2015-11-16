package nl.jappieklooster.hw.ec

object MateSelection {

	def tournamentWinIsParent(
		valuator : Valuable => Int, bachelors: Population
	) : PairedPopulation = {
		def tournement(contender:(IMember, IMember, IMember, IMember)):Pair = {
			val mother = if (
				valuator(contender._1) > valuator(contender._2)
			) contender._1 else contender._2
			val father = if(
				valuator(contender._3) > valuator(contender._4)
			) contender._3 else contender._4
			Pair(mother, father)
		}
		def matching(a:Seq[IMember], b:Seq[IMember]) = a.zip(b.reverse).grouped(2)
		val mem = bachelors.members
		val half = Math.floor(mem.length/2).toInt
		val revmem = mem.take(half) ++ mem.drop(half).reverse

		val matchup = matching(mem,mem) ++ matching(revmem, revmem)
		// because we used reverse half can be dropped

		// creating the final structure
		val brothers = matchup.map(x => {

			// flatten the thing to give myself not a headache later
			tournement((x.head._1, x.head._1, x.last._1, x.last._2))
		})
		val sisters = matchup.map(x => {
			// flatten the thing to give myself not a headache later
			tournement((x.head._1, x.last._1, x.last._1, x.head._2))
		})

		// the actuall tournement
		return PairedPopulation(sisters.toSeq ++ brothers, bachelors.memberFactory)
	}
}

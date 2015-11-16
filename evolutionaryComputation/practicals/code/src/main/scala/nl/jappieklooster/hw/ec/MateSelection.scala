package nl.jappieklooster.hw.ec

object MateSelection {

	def tournamentWinIsParent(
		valuator : Valuable => Int, bachelors: Population
	) : PairedPopulation = {
		def tournement(contenders:Seq[IMember]):Pair = {
			def sorter(a:IMember, b:IMember) = valuator(a) > valuator(b)

			val father = contenders.drop(2).sortWith(sorter)
			val mother = contenders.sortWith(sorter)
			Pair(father.headOption.getOrElse(mother.head), mother.head)
		}
		val mem = bachelors.members
		val matchup = mem.grouped(4)
		// first matchup the members into a tournement
		// to fill 1 pair we need 4 contestents (as 2 will lose)
		// because we used reverse half can be dropped

		// creating the final structure
		val brothers = matchup.map(tournement)
		val sisters = matchup.map(x => {
			tournement(x.head +: x.drop(1).reverse)
		}).toSeq

		// the actuall tournement
		return PairedPopulation(sisters ++ brothers, bachelors.memberFactory)
	}
}

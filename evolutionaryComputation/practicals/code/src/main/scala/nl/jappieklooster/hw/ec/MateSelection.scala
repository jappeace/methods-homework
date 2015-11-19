package nl.jappieklooster.hw.ec

import scala.util.Random
trait IMateSelector{
	def selectFrom(bachelors:Population):PairedPopulation
}
abstract class TournamentSelector(valuator : IHasFitness => Int) extends IMateSelector{
	val tournement = MateSelection.pickBestWith(valuator)_
	override def selectFrom(bachelors: Population): PairedPopulation =
		PairedPopulation(tournementSelect(bachelors.members), bachelors.memberFactory)
	protected def tournementSelect(bachelors:Seq[IMember]):Seq[Pair]
}

class CompeteWithRandomTournement(random:Random, valuator : IHasFitness => Int)
		extends TournamentSelector(valuator){
	override protected def tournementSelect(bachelors: Seq[IMember]): Seq[Pair] = {
		val potentialFathers = bachelors.grouped(2)
		val potentialMothers = random.shuffle(bachelors).grouped(2)
		potentialFathers.zip(potentialMothers).map(
			x=> tournement(x._1.head, x._1.last, x._2.head, x._2.last)
		).toSeq
	}

}
class CompeteWithReversedTournement(valuator : IHasFitness => Int)
		extends TournamentSelector(valuator){

	protected override def tournementSelect(bach:Seq[IMember]):Seq[Pair] = {
		val matchup = bach.
				// first matchup the bachbers into a tournement
				// to fill 1 pair we need 4 contestents (as 2 will lose)
				zip(
					bach.slice(1, bach.length) :+ bach.head
				).zip(
					bach.reverse
				).zip(
					(bach.slice(1, bach.length) :+ bach.head).reverse
				)
				// because we used reverse half can be dropped
				.drop(Math.floor(bach.length/2).toInt)

		// creating the final structure
		return matchup.map(x => {
			// flatten the thing to give myself not a headache later
			tournement(x._1._1._1, x._1._1._2, x._1._2, x._2)
		}) ++
		 matchup.map(x => {
			// flatten the thing to give myself not a headache later
			tournement(x._1._1._1, x._2, x._1._1._2, x._2)
		})
	}
}
object MateSelection {
	def pickBestWith(valuator : IHasFitness => Int)
			(one:IMember, two:IMember, three:IMember, four:IMember):Pair ={
		def fight(x:IMember, y:IMember) = if (valuator(x) > valuator(y)) x else y
		Pair(fight(one,two), fight(three, four))
	}
	def createCompeteWithReversedTournement(valuator : IHasFitness => Int) =
		new CompeteWithReversedTournement(valuator)
	def createCompeteWithRandomTournement(rand:Random)(valuator : IHasFitness => Int) =
		new CompeteWithRandomTournement(rand, valuator)
}

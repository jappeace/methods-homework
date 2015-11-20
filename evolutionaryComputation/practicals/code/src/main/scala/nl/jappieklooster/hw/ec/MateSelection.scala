package nl.jappieklooster.hw.ec

import scala.util.Random
trait IMateSelector{
	def selectFrom(bachelors:Population):PairedPopulation
}
abstract class TournamentSelector(valuator : IHasFitness => Int) extends IMateSelector{
	val tournement = MateSelection.pickBestWith(valuator)_
	override def selectFrom(bachelors: Population): PairedPopulation =
		PairedPopulation(tournementSelect(bachelors), bachelors.memberFactory)
	protected def tournementSelect(bachelors:Population):Seq[Pair]
}

class CompeteWithRandomTournement(random:Random, valuator : IHasFitness => Int)
		extends TournamentSelector(valuator){
	override protected def tournementSelect(bachelors: Population): Seq[Pair] = {
		val potentialFathers = bachelors.grouped(2)
		val potentialMothers = random.shuffle(bachelors.members).grouped(2)
		potentialFathers.zip(potentialMothers).map(
			x=> Pair(
				tournement(x._1.head, x._1.last),
				tournement(x._2.head, x._2.last)
			)
		).toSeq
	}

}
object MateSelection {
	def pickBestWith(valuator : IHasFitness => Int)(x:IMember, y:IMember) = if (valuator(x) > valuator(y)) x else y
	def createCompeteWithRandomTournement(rand:Random)(valuator : IHasFitness => Int) =
		new CompeteWithRandomTournement(rand, valuator)
}

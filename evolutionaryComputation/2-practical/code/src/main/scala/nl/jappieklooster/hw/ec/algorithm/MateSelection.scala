// Evolutionary Computing Analyzer, it analyzes various genetic algoritms
// Copyright (C) 2015 Jappie Klooster

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/\>.
package nl.jappieklooster.hw.ec.algorithm

import nl.jappieklooster.hw.ec.model
import nl.jappieklooster.hw.ec.model.{IMember, PairedPopulation, Population}

import scala.util.Random
trait IMateSelector{
	def selectFrom(bachelors:Population):PairedPopulation
}
abstract class TournamentSelector extends IMateSelector{
	override def selectFrom(bachelors: Population): PairedPopulation =
		PairedPopulation(tournementSelect(bachelors), bachelors.memberFactory)
	protected def tournementSelect(bachelors:Population):Seq[model.Pair]
}

class CompeteWithRandomTournement(random:Random)
		extends TournamentSelector{
	override protected def tournementSelect(bachelors: Population) = {
		val potentialFathers = bachelors.grouped(2)
		val potentialMothers = random.shuffle(bachelors.members).grouped(2)
		potentialFathers.zip(potentialMothers).map(
			x=> model.Pair(
				MateSelection.pickBest(x._1.head, x._1.last),
				MateSelection.pickBest(x._2.head, x._2.last)
			)
		).toSeq
	}

}
object MateSelection {
	def pickBest(x:IMember, y:IMember) = if (x.fitness > y.fitness) x else y
	def createCompeteWithRandomTournement(rand:Random) =
		new CompeteWithRandomTournement(rand)
}

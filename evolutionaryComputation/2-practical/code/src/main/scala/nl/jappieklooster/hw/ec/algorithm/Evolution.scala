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

import nl.jappieklooster.hw.ec.model.{PairedPopulation, Population}

import Evolution._

import scala.annotation.tailrec

class Evolution(
	mateSelector:IMateSelector,
	offSpringGenerator: OffspringGenerator,
	/**
	 * fittest filter, (valuation, parents, children) => fittestpop
	 */
	fittestFilter: FittestFilter,

	/**
	 * If there already exists a really good solution you're
	 * happy with you can use this so you don't have to wait untill
	 * convergence
	 */
	hasGoodEnoughSolution: SolutionJudge = SolutionJudge.badSolution
) {
	private def step(parents:Population):Population = fittestFilter(
			parents,
			offSpringGenerator(
				mateSelector.selectFrom(
					parents
				)
			)
		)
	def startGenetic(seed:Population): Seq[Population]= genetic(
		Seq(seed)
	)
	@tailrec
	private def genetic(prev:Seq[Population]): Seq[Population]={
		val parents = prev.last
		if(hasGoodEnoughSolution(parents)){
			return prev
		}
		val children = step(parents)

		if (children == parents) {
			return prev
		}
		genetic(prev :+ children)
	}
}

object Evolution{
	type OffspringGenerator = PairedPopulation => Population
	type FittestFilter = (Population, Population) => Population
	type SolutionJudge = Population => Boolean
	object SolutionJudge{
		def badSolution(x:Population) = false
	}
}
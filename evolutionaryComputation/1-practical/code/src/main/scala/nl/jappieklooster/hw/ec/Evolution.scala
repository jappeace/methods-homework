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
package nl.jappieklooster.hw.ec

class Evolution(
	val evaluation: FitnessEvaluator,
	mateSelector:IMateSelector,
	offSpringGenerator: PairedPopulation => Population,
	/**
	 * fittest filter, (valuation, parents, children) => fittestpop
	 */
	fittestFilter: (Population, Population) => Population,
	hasGoodEnoughSolution: Population => Boolean
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


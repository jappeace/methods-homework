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

object FittestFilter {
	/** drop the parents */
	def killParents(
		parents : Population,
		children : Population
	) : Population = {
		children
	}

	def truncateElitism(
			parents : Population,
			children : Population
			) : Population = {
		val combined = parents ++ children
		// end up sorting anyways, no idea why you would do tournement selection
		val sorted = combined.sortWith((x, y) => x.fitness > y.fitness)
		parents.copy(members = sorted.take(parents.length))
	}

	def tournementElitism(
		parents : Population,
		children : Population
	): Population={
		val combined = parents.members.zip(children.members)
		Population(
			combined.map(x=> MateSelection.pickBest(x._1, x._2)),
			parents.memberFactory
		)
	}


}

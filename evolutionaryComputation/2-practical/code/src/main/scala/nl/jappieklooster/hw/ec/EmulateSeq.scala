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

/**
 * a trait that passes all behavior onto an attribute (baseSeq)
 * @tparam T
 */
trait EmulateSeq[+T] extends Seq[T] {
	def baseSeq:Seq[T]
	override def length: Int = baseSeq.length
	override def iterator: Iterator[T] = baseSeq.iterator
	override def apply(indx:Int):T = baseSeq.apply(indx)
}

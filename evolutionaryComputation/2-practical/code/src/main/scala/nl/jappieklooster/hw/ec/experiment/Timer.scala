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
package nl.jappieklooster.hw.ec.experiment

object Timer {
	def measure[R](block: => R): Computation[R] = {
		val t0 = System.nanoTime()
		val result = block    // call-by-name
		val t1 = System.nanoTime()
		Computation(result, t1-t0)
	}
}
case class Computation[R](result:R, duration:Long){
	lazy val seconds:Double = duration / 1000000000.0
}
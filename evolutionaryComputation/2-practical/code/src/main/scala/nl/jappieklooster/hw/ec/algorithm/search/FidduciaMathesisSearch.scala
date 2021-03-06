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

package nl.jappieklooster.hw.ec.algorithm.search

import Search._
import nl.jappieklooster.hw.ec.algorithm.OffspringGenerator
import nl.jappieklooster.hw.ec.model.{IMember, Graph, Vertex}

import scala.annotation.tailrec

/**
 * Used as a link list like structure in the map.
 * Now its possible to manipulate the list in place with a reference to this cell
 * Its impossible to use std structures because they encapsulte this away.
 *
 * Note that the connections are mutable on intention.
 *
 * @param item the vertex
 * @param next next cell
 * @param previous previous cell
 */
class Cell(val item:Vertex, var gain:Int, private var _next:Option[Cell]=None, var previous:Option[Cell]=None){

	def next = _next
	def next_=(option: Option[Cell]) = {
		_next = option
		for(opt <- option){
			if(item == opt.item){
				throw new IllegalArgumentException("Can't say next is yourself, thats stupid")
			}
		}
	}
}

/**
 * This bucket class serves as an encapsulation of several related mutable fields
 * Its a mutable structure based on the immutable map, however it encapsulates this
 * in combination of the max-gain variable and a more useful (for this algorithm at least) pop method
 * @param partitioning
 * @param cells
 * @param as
 * @return
 */
class Bucket(
		private var cellMap:Array[Option[Cell]],
		private val lowOffset:Int,
		private var maxGain:Int
	) {
	def gain = maxGain
	def offset(gain:Int):Int = {
		val result = gain-lowOffset
		result
	}
	final def pop:Option[Cell] = {
		if(isEmpty){
			return None
		}
		if (cellMap(offset(maxGain)).isEmpty) {
			maxGain = maxGain - 1
			return pop
		}
		remove(maxGain)
	}
	final def remove(gain:Int):Option[Cell]={
		val result = cellMap(offset(gain))
		cellMap(offset(gain)) = None
		for(cell <- result; next <- cell.next){
			next.previous = None
			cellMap(offset(gain)) = cell.next
		}
		result

	}
	def isEmpty = offset(maxGain) == -1
	def insert(cell: Cell) = {
		// Important, for the pop operation to be constant
		if(cell.gain > maxGain){
			maxGain = cell.gain
		}
		// first element has no previous
		cell.previous = None

		val index = offset(cell.gain)
		if(index < 0){
			println("ohoh")
		}
		// cell may or may not have a next
		cell.next = cellMap(index)

		// we don't care, the for will only be executed if there is a next
		for(existing <- cell.next){
			existing.previous = Some(cell)
		}

		// finally update the map (its a var so += is allowed).
		cellMap(offset(cell.gain)) = Some(cell)
	}
}
object Bucket{
	val margin = 50 // just costs more memory nothing else
	def apply(partitioning:String, cells:Array[Cell], as:Char):Bucket = {
		val filtered = cells.
				// select only the elements of a certain partition
				filter(x => partitioning(x.item.id) == as).
				// we just make a map off it for now and arrayifie that later
				groupBy(_.gain).
				// group by creates a map with arrays of elements, we remove the
				// array with mapvalues into the linked list structure of cell
				mapValues(
					_.foldLeft[Cell](null)((a,b)=>{
						// easy hack to avoid an "empty" head, doing an empty
						// head just makes things more complicated
						if(a==null){
							b
						}else{
							// it goes right to left? Testing shows it does.
							a.previous = Some(b)
							b.next = Some(a)
							b
						}
					})
				)
		val min = filtered.keySet.min - (margin/2)
		val max = filtered.keySet.max + (margin/2)
		new Bucket(
			// map the map to an array, get will return none if there is no
			// element, this is what we want.
			min.to(max).map(x=>filtered.get(x)).toArray,
			min,
			max
		)
	}
}
/**
 * Using the Fiduccia Mattheyses algorithm to get an O(n)-ish time localsearch
 * heuristic.
 *
 * Paper: https://github.com/tomek82/EPPart_Pub/blob/master/Printed/Fiduccia,%20Mattheyses%20-%20A%20Linear-Time%20Heuristic%20for%20Improving%20Network%20Partitions.pdf
 *
 * Test show that it takes about ~5 ish passes to find the local optimum
 * Combined with GLS it performs extremely well. (although GLS is slow).
 * @param graph
 * @param memberFactory
 */
class FidduciaMathesisSearch(graph:Graph, memberFactory:String => IMember) extends SearchMethod {


	/**
	 * An instance of this class is a function of type SearchMethod. On calling
	 * this class as a function this method is called.
	 * @param member
	 * @return
	 */
	def apply(member:IMember):IMember = {
		val partitioning = member.genes

		// for this to work all the cells need to be the same. ie we can use a cell instead
		// of a vertex. So we can manipulate the buckets without knowing the gain.
		// we can find the proper cell of a vertex by calling this array by vertex.id
		val cells = graph.verteci.map(x=> {
			val part = partitioning(x.id)
			val gain = x.connections.count(p => partitioning(p) != part) - x.connections.count(p => partitioning(p) == part)
			new Cell(x, gain)
		})
		val bucketZero = Bucket(partitioning, cells, '0')
		val bucketOne = Bucket(partitioning, cells, '1')
		def getbucket(char:Char) = if(char=='1') bucketOne else bucketZero
		val freeCells:Array[Option[Cell]] = cells.map(Some(_))

		// we wil store the best found in here, we just assume we already have the
		// best as comparison
		var best = member
		algorithm(new StringBuilder(partitioning))

		@tailrec
		def algorithm(part:StringBuilder):Unit = {
			// assuming the buckets remain balanced, we can do this
			if(bucketOne.isEmpty){
				return
			}

			// best first, but we do both to keep balance
			// (if we don't the algorithm will move everything to one parition)
			val buckets = if(bucketZero.gain > bucketOne.gain){
				Seq(bucketZero, bucketOne)
			}else{
				Seq(bucketOne, bucketZero)
			}

			// crossing involves a lot of adminstration
			def cross(cell:Cell) = {
				val current = part(cell.item.id)

				// because of the cross the vertex connections' gain will change
				// we need to update this
				val modified = cell.item.connections.filter(
					freeCells(_).isDefined
				).map(freeCells(_).get)

				modified.foreach(mdcl=>updateModifiedCell(part(mdcl.item.id), current, mdcl))
				part.setCharAt(cell.item.id, OffspringGenerator.flipBit(current))

				// makes the filter seen earlier more powerfull
				freeCells(cell.item.id) = None
			}
			buckets.foreach(b=>for(cell <- b.pop){
				cross(cell)
			})
			val contester = memberFactory(part.mkString)

			if(contester.fitness > best.fitness){
				// ding ding ding, we have a new champion.
				best = contester
			}
			algorithm(part)
		}
		def updateModifiedCell(modPart:Char, currentPart:Char, modcell: Cell):Unit ={
			val change = if(modPart == currentPart) 1 else -1
			modcell.gain += change
			val bucket = getbucket(modPart)
			if(modcell.previous.isEmpty){
				//first is being tracked in the buckets themselves.
				//so the bucket keeps a refernce to this modcell, so we need
				//to remove this modcell
				bucket.remove(modcell.gain - change)
			}
			// in either case we need to fix the linklist like cellstructure
			// ie reatach the cells in a way so that the current cell is no more
			for(prev <- modcell.previous){
				prev.next = modcell.next
			}
			for(next <- modcell.next){
				next.previous = modcell.previous
			}
			// the modcell links will be updated by the insert operation
			bucket.insert(modcell)
		}
		return best
	}
}

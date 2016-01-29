package nl.jappieklooster.hw.ec.algorithm.search

import Search._
import nl.jappieklooster.hw.ec.model.{IMember, Graph, Vertex}

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
class Cell(val item:Vertex, var gain:Int, var next:Option[Cell]=None, var previous:Option[Cell]=None){}

/**
 * In the original paper the bucket was an array indicate the gain that could be
 * gotten. However I take enough with a map because it is constant-ish and I
 * can generate it with std scala methods (although I could fold the map again into
 * an array I don't think I'll save time by doing that)
 *
 * This bucket class serves as an encapusaltion of several related mutable fields
 * Its a mutable sturcture based on the imutable map, however it encapsulates this
 * in combination of the maxgain variable and a more usefull (for this algorithm atleast) pop method
 * @param partitioning
 * @param cells
 * @param as
 * @return
 */
class Bucket(private var cellMap:Map[Int,Cell]) {
	private var maxGain = cellMap.keySet.max
	def gain = maxGain
	def pop:Option[Cell] = {
		if(cellMap.isEmpty){
			return None
		}
		if(!cellMap.contains(maxGain)){
			maxGain = maxGain -1
			return pop
		}
		val result = cellMap(maxGain)
		cellMap -= maxGain
		for(next <- result.next){
			next.previous = None
			cellMap += (maxGain -> next)
		}
		Some(result)
	}
	def isEmpty = cellMap.isEmpty
}
object Bucket{
	def apply(partitioning:String, cells:Array[Cell], as:Char):Bucket = {
		new Bucket(
			cells.filter(x => partitioning(x.item.id) == as).groupBy(_.gain).
			mapValues(
				_.foldLeft[Cell](null)((a,b)=>{
					if(a==null){
						b
					}else{
						a.next = Some(b)
						b.previous = Some(a)
						b
					}
				})
			)
		)
	}
}
/**
 * Using the Fiduccia Mattheyses algorithm to get an O(n)-ish time localsearch
 * heuristic.
 *
 * Paper: https://github.com/tomek82/EPPart_Pub/blob/master/Printed/Fiduccia,%20Mattheyses%20-%20A%20Linear-Time%20Heuristic%20for%20Improving%20Network%20Partitions.pdf
 *
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
		val cells = graph.verteci.map(x=> {
			val part = partitioning(x.id)
			val gain = x.connections.count(p => partitioning(p) != part) - x.connections.count(p => partitioning(p) == part)
			new Cell(x, gain)
		})
		val bucketZero = Bucket(partitioning, cells, '0')
		val bucketOne = Bucket(partitioning, cells, '1')
		val freeCells = cells.map(Some(_))

		def move(part:String):Seq[IMember] = {
			if(bucketOne.isEmpty){
				return Nil
			}
			val buckets = if(bucketZero.gain > bucketOne.gain){
				(bucketZero, bucketOne)
			}else{
				(bucketOne, bucketZero)
			}
			for(cell <- buckets._1.pop){
				val current = part(cell.item.id)
				val modified = cell.item.connections.filter(freeCells(_).isDefined).map(freeCells(_).get)
				for(modcell <- modified){
					val change = if(part(modcell.item.id) == current) 1 else -1
					modcell.gain += change
					for(prev <- modcell.previous){
						prev.next = modcell.next
					}
					for(next <- modcell.next){
						next.previous = modcell.previous
					}
					modcell.previous = None
					modcell.next = None
				}
			}
			Nil
		}
		member
	}
}

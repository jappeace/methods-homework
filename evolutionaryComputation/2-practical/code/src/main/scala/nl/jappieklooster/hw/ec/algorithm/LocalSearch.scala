package nl.jappieklooster.hw.ec.algorithm

import nl.jappieklooster.hw.ec.model.{Population, IMember, Graph}
import scala.annotation.tailrec
import scala.util.Random

class LocalSearch(method:IMember => IMember, stopCondition:(IMember, IMember)=>Boolean = (a,b)=> a==b){
	@tailrec
	final def search(member: IMember):IMember = {
		val result = method(member)
		if(stopCondition(result,member)){
			return result
		}else{
			return search(result)
		}
	}
}
object LocalSearch {

	/**
	* Swap characters at specified positions.
	*/
	def swapChar(partitioning:String)(one:Int, two:Int):String = { // how is this not a standard function?!
		val builder = new StringBuilder(partitioning)
		builder.setCharAt(one, partitioning(two))
		builder.setCharAt(two, partitioning(one))
		builder.mkString
	}
	/**
	* The local search algorithm we use is the vertex
	* swap neighborhood search (VSN). In VSN a neighboring solution is generated by swapping
	* two vertices, one from each partitioning.
	*/
	def vertexSwapFirstImprovement(graph:Graph, memberFactory:String => IMember)(member:IMember):IMember = {
		val partitioning = member.genes

		// swapping of vertex in partitions
		val swapVertex = swapChar(member.genes) _

		// I just don't know a good way to do this with recursion or combbinators
		for(index <- 0.to(graph.verteci.length)){

			val vert = graph.verteci(index)
			val options = vert.connections.filter(conindx => partitioning(index) != partitioning(conindx))

			var lastMember:IMember = null // optimization, prevents member creation and valuation twice

			// first improvement variant
			val result = options.find(conindx => {
				lastMember = memberFactory(swapVertex(conindx, index))
				lastMember.fitness > member.fitness
			})
			if(!result.isEmpty){
				return lastMember
			}
		}
		member
	}
}

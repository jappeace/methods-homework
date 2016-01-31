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
package nl.jappieklooster.hw.ec.model

case class Vertex(id:Int, connections:Seq[Int]) {
}

object Vertex{
	def create(id:Int, connections:Seq[Int]) = new Vertex(id,connections)
	def pruned(id:Int, connections:Seq[Int]) = new Vertex(id, connections.filter(id < _))
}
case class Graph(verteci:Array[Vertex]){
	override def toString = s"Graph(edges:$edgeCount){${
		verteci.foldLeft("")((p, v) => p + s"(${v.id}:{${v.connections.mkString(",")}}) ")
	}}"
	lazy val edgeCount = verteci.map(_.connections.length).sum
}
object Graph{
	def create(vertexFactory:(Int,Seq[Int])=>Vertex)(elements:Seq[Int]*) :Graph = Graph(
		elements.zipWithIndex.map(
			x=>vertexFactory(x._2, x._1)
		).toArray
	)
}
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
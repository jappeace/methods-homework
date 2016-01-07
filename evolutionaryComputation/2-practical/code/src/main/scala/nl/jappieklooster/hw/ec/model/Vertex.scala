package nl.jappieklooster.hw.ec.model

case class Vertex(id:Int, connections:Seq[Int]) {
}

case class Graph(verteci:Array[Vertex]){
	override def toString = "Graph{"+ verteci.foldLeft("")((p, v) => p + s"(${v.id}:{${v.connections.mkString(",")}}) ") +"}"
}
object Graph{
	def create(elements:Seq[Int]*) :Graph = Graph(
		elements.zipWithIndex.map(
			x=> Vertex(x._2, x._1)
		).toArray
	)
}
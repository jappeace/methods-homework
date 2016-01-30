package nl.jappieklooster.hw.ec.tests

import nl.jappieklooster.hw.ec.algorithm.Evaluation
import nl.jappieklooster.hw.ec.algorithm.search.{VertexSwapFirstImprovement, Search}
import org.scalatest.{Matchers, FlatSpec}
import nl.jappieklooster.hw.ec.model.{Member, Graph}
import Search._

class SearchTest extends FlatSpec with Matchers{

	val graph = Graph.create(
		Seq(2),
		Seq(0),
		Nil,
		Seq(0,1)
		)

	val valuation = Evaluation.graphValuation(graph) _
	"swapChar " should " swap chars " in {
		swapChar("jappie")(1,4) should be("jippae")
		swapChar("1001")(0,1) should be("0101")

		val function = swapChar("jappie") _

		function(1,4) should be ("jippae")
		function(0,1) should be ("ajppie")
	}

	val testingString = "1001"
	"the string we're testing against" should " have another higher value" in {
		val higherLocal = "0101"
		valuation(testingString) should be(1)
		valuation(higherLocal) should be(2)
	}

	"firstVertexSwap" should "find a better solution" in {
		val memberfactory = (str:String) => Member(valuation(str), str)
		val localsearch = new VertexSwapFirstImprovement(graph, memberfactory)

		val testingMember = memberfactory(testingString)
		val searchResult = localsearch(testingMember)

		// the spec doesn't care about the precise value, as long as its better
		searchResult.fitness should be > testingMember.fitness
	}
}

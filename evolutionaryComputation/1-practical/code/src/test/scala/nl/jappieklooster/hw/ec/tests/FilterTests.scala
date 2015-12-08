package nl.jappieklooster.hw.ec.tests

import nl.jappieklooster.hw.ec.{IMember, Member, Population}
import org.scalatest.{Matchers, FlatSpec}

import nl.jappieklooster.hw.ec.FittestFilter._

class FilterTests extends FlatSpec with Matchers{
	def createPop(people:Seq[IMember]) = Population(people,null)
	"trucnate select " should "return the best " in {
		val expected = createPop(List(Member(10,"d"), Member(8,"c")))
		truncateElitism(
			createPop(List(Member(4,"a"), Member(10,"d"))),
			createPop(List(Member(2,"b"), Member(8,"c")))
		) should be (expected)
	}

}

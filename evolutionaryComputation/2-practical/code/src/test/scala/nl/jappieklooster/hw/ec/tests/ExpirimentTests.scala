package nl.jappieklooster.hw.ec.tests

import nl.jappieklooster.hw.ec.model.Member
import nl.jappieklooster.hw.ec.model.{Member, Population}
import org.scalatest.{Matchers, FlatSpec}
import nl.jappieklooster.hw.ec.Experiment._

class ExpirimentTests extends FlatSpec with Matchers{

	def createMember(str:Seq[String]) = Population(str.map(x=>Member(1,x)), null)
	"has good enough result " should " not be true " in {
		hasGoodEnoughSolution(createMember(Seq("0111", "1010", "1010", "0000", "1000"))) should be (false)
	}
	"has good enough result " should " be true " in {
		hasGoodEnoughSolution(createMember(Seq("0111", "1010", "111111", "1010", "0000", "1000"))) should be (true)
	}

}

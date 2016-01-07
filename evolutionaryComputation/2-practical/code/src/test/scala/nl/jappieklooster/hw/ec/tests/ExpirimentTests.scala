package nl.jappieklooster.hw.ec.tests

import nl.jappieklooster.hw.ec.experiment.Experiment
import nl.jappieklooster.hw.ec.model.Member
import nl.jappieklooster.hw.ec.model.{Member, Population}
import org.scalatest.{Matchers, FlatSpec}

class ExpirimentTests extends FlatSpec with Matchers{

	def createMember(str:Seq[String]):Population = Population(str.map(x=>Member(1,x)), null)
	"has good enough result " should " not be true " in {
		val members  = createMember(List("0111", "1010", "1010", "0000", "1000"))
		Experiment.hasGoodEnoughSolution(
			members
		) should be (false)
	}
	"has good enough result " should " be true " in {
		Experiment.hasGoodEnoughSolution(createMember(Seq("0111", "1010", "111111", "1010", "0000", "1000"))) should be (true)
	}

}

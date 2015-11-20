package nl.jappieklooster.hw.ec.tests

import nl.jappieklooster.hw.ec._
import org.scalatest.{Matchers, FlatSpec}
import org.slf4j.LoggerFactory
import util.Random

class MateSelectionTests extends FlatSpec with Matchers{
	val log = LoggerFactory.getLogger(this.getClass)
	// rig random so we know what will happen
	val rand = new Random(5)
	// again we rig the show to make sure the right things happen
	def valuator(iHasFitness: IHasFitness) = 5

	def createSubject = new CompeteWithRandomTournement(rand, valuator)
	"the tournament function when rigged" should "always let the rihgt side win " in {
		val subject = createSubject
		val one = SinglyMember("1")
		val two = SinglyMember("2")
		val three = SinglyMember("3")
		val four = SinglyMember("4")
		subject.tournement(one,two) should be (two)
	}
	"the random turnement when rigged " should "produce the following sequent " in {
		val subject = createSubject
		val one = SinglyMember("1")
		val two = SinglyMember("2")
		val three = SinglyMember("3")
		val four = SinglyMember("4")
		// with the rigging of this rand, the shuffle will produce List(4,1,2,3)
		// as can been seen from the other test the right side will always win.
		// so expected is:
		val expected = PairedPopulation(List(Pair(two, one), Pair(four, three)), null)
		subject.selectFrom(Population(List(one,two,three,four), null)) should be (expected)
	}

}

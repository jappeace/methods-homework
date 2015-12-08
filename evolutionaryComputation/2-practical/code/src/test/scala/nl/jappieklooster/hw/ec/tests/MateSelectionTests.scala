package nl.jappieklooster.hw.ec.tests

import nl.jappieklooster.hw.ec._
import org.scalatest.{Matchers, FlatSpec}
import org.slf4j.LoggerFactory
import util.Random

class MateSelectionTests extends FlatSpec with Matchers{
	val log = LoggerFactory.getLogger(this.getClass)
	// rig random so we know what will happen
	def rand = new Random(5)
	// again we rig the show to make sure the right things happen

	def createMember(s:String) = Member(5,s)
	def createMember(i:Int, s:String) = Member(i,s)
	def createSubject = new CompeteWithRandomTournement(rand)
	"the tournament function when rigged" should "always let the rihgt side win " in {
		val one = createMember("1")
		val two = createMember("2")
		MateSelection.pickBest(one,two) should be (two)
	}
	"the random turnement when rigged " should "produce the following sequent " in {
		val subject = createSubject
		val one = createMember("1")
		val two = createMember("2")
		val three = createMember("3")
		val four = createMember("4")
		// with the rigging of this rand, the shuffle will produce List(4,1,2,3)
		// as can been seen from the other test the right side will always win.
		// so expected is:
		val expected = PairedPopulation(List(Pair(two, one), Pair(four, three)), null)
		subject.selectFrom(Population(List(one,two,three,four), null)) should be (expected)
	}
	"the random turnement when rigged " should "produce the best two as winners " in {
		val subject = createSubject
		val one = createMember(4, "a")
		val two = createMember(5, "b")
		val three = createMember(8, "c")
		val four = createMember(3, "d")
		// with the rigging of this rand, the shuffle will produce List(4,1,2,3)
		// as can been seen from the other test the right side will always win.
		// so expected is:
		val expected = PairedPopulation(List(Pair(two, one), Pair(three, three)), null)
		subject.selectFrom(Population(List(one,two,three,four), null)) should be (expected)
	}

}

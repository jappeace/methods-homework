package nl.jappieklooster.hw.ec.tests

import nl.jappieklooster.hw.ec._
import org.scalatest.{Matchers, FlatSpec}

import scala.util.Random

class CrossoverTests extends FlatSpec with Matchers{
	def rand = new Random(5)
	def mem(s:String) = Member(1,s)
	def twoPoint(one:Member, two:Member) = OffspringGenerator.twoPointCross(rand)(PairedPopulation(Seq(Pair(one,two)), mem))
	"2 point cross " should " work correct when rigged " ignore{
		val father = mem("abcdefg")
		val mother = mem("1234567")
		// 2,4
		twoPoint(father,mother) should be(mem("ab34efg"))
		twoPoint(father,mother) should be(mem("ab34efg"))
		twoPoint(father,mother) should be(mem("ab34efg"))
		// 4,0
		twoPoint(father,mother) should be(mem("1234efg"))

	}
}

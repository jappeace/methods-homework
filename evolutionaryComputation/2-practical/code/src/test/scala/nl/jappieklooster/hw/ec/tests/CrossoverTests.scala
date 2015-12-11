package nl.jappieklooster.hw.ec.tests

import nl.jappieklooster.hw.ec.model.OffspringGenerator
import OffspringGenerator._
import org.scalatest.{Matchers, FlatSpec}

import scala.util.Random

class CrossoverTests extends FlatSpec with Matchers{
	def invertCross(a:IndexedSeq[(Char,Char)],b:Int,c:Int) = twoPointCross(inverResult, a,b,c).mkString
	"2 point cross " should " work unbiased" in{
		val parents = "abcdefg".zip("1234567")
		// normal crosses...
		invertCross(parents,1,3)should be("a23defg")
		invertCross(parents,0,3)should be("123defg")

		// when the random generates the first number higher then the second
		// we excpect the thing to become reversered.
		invertCross(parents,3,1)should be("1bc4567")
		invertCross(parents,3,0)should be("abc4567")
	}

	def biasedCross(a:IndexedSeq[(Char,Char)],b:Int,c:Int) = twoPointCross(keepSelected, a,b,c).mkString
	"2 point cross " should " work biased" in{
		val parents = "abcdefg".zip("1234567")
		// normal crosses...
		biasedCross(parents,1,3)should be("a23defg")
		biasedCross(parents,0,3)should be("123defg")

		// when the random generates the first number higher then the second
		// we excpect the thing to become reversered.
		biasedCross(parents,3,1)should be("a23defg")
		biasedCross(parents,3,0)should be("123defg")
	}
}

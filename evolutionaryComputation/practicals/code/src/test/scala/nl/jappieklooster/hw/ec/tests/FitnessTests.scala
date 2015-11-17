package nl.jappieklooster.hw.ec.tests

import nl.jappieklooster.hw.ec.IHasFitness
import nl.jappieklooster.hw.ec.Fitness
import org.scalatest.{Matchers, FlatSpec}

class FitnessTests extends FlatSpec with Matchers{

	/**
	 * create a known subject which we can count manually
	 */
	val subject = new IHasFitness {
		val str = "1111" + "0111" + "1010" + "1000" + "0000"
		//		   1234     5678     9012     3
		// so linearly scaled = 1+2+3+4 + 6+7+8 + 9+ 11+ 13 = 64
		// decept: 4	+	0	+ 	1	+	2	+ 3 = 10
		// non: 	4	+	0	+ 	0.5	+	1	+ 1.5 = 7

		override def getFitness: String = str
	}

	"Linearly counting fitness " should "count subject as 10" in {
		Fitness.uniformlyScaledCountOnes(subject) should be (10)
	}
	"Linearly scaled fitness " should "count subject as 64" in {
		Fitness.linearlyScaledCountOnes(subject) should be (64)
	}

	val deciptive = List(4f,0f,1f,2f,3f)
	"deceptive trap fitness" should "count subject as 10" in {
		Fitness.blockValuation(deciptive)(subject) should be (10)
	}
	val nondecept = List(4f, 0f, 0.5f, 1f, 1.5f)
	"nondeceptive trap fitness" should "count subject as 7" in {
		Fitness.blockValuation(nondecept)(subject) should be (7)
	}
}

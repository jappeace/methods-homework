package nl.jappieklooster.hw.ec.tests

import nl.jappieklooster.hw.ec.IHasFitness
import nl.jappieklooster.hw.ec.Fitness
import org.scalatest.{Matchers, FlatSpec}

class FitnessTests extends FlatSpec with Matchers{

	/**
	 * create a known subject which we can count manually
	 */
	val subject = new IHasFitness {
		val str = "11111" + "01111" + "10110" + "00001" + "00000" + "10000"
		//			12345	  67890		12345	  67890		12345	  6
		//so linearly =1+2+3+4+5+7+8+9+10+11+13+14+20+26 = 133

		override def getFitness: String = str
	}

	"Linearly counting fitness " should "count subject as 14" in {
		Fitness.uniformlyScaledCountOnes(subject) should be (14)
	}
	"Linearly scaled fitness " should "count subject as 133" in {
		Fitness.linearlyScaledCountOnes(subject) should be (133)
	}

	val deciptive = List(4f,0f,1f,2f,3f)
	"deceptive trap fitness" should "count subject as" in {
		Fitness.blockValuation(deciptive)(subject) should be (1)
	}
}

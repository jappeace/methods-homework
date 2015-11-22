package nl.jappieklooster.hw.ec.tests

import nl.jappieklooster.hw.ec.IHasFitness
import nl.jappieklooster.hw.ec.Fitness
import org.scalatest.{Matchers, FlatSpec}

class FitnessTests extends FlatSpec with Matchers{

	/**
	 * create a known subject which we can count manually
	 */
	val subject =
		"1111" + "0111" + "1010" + "1000" + "0000"
		//		   1234     5678     9012     3
		// so linearly scaled = 1+2+3+4 + 6+7+8 + 9+ 11+ 13 = 64
		// decept: 4	+	0	+ 	1	+	2	+ 3 = 10
		// non: 	4	+	0	+ 	0.5	+	1	+ 1.5 = 7


	"Linearly counting fitness " should "count subject as 10" in {
		Fitness.uniformlyScaledCountOnes(subject) should be (10)
	}
	"Linearly scaled fitness " should "count subject as 64" in {
		Fitness.linearlyScaledCountOnes(subject) should be (64)
	}

	val deceptive = Fitness.blockValuation(List(4f,0f,1f,2f,3f)) _
	"deceptive trap fitness" should "count subject as 10" in {
		deceptive(subject) should be (10)
	}
	"decipte trap fitness " should "count these infividual elements correctly " in{
		/*
		* I suepected something was wrong with the trap function because all trap
		* GA's failed to find solution wich didn't make much sense (wouldn't learn
		* much from a function that doesn't work).
		* These tests proove that
		 */
		deceptive("1111") should be (4)
		deceptive("0111") should be (0)
		deceptive("0011") should be (1f)
		deceptive("0001") should be (2f)
		deceptive("0000") should be (3f)
	}
	"trap function" should "have no trouble with wrongly block sized input " in {
		deceptive("1111" + "0111" + "1010" + "1000" + "00") should be (10)  // because it should count the ones
	}
	val nondecept = Fitness.blockValuation(List(4f, 0f, 0.5f, 1f, 1.5f)) _
	"nondeceptive trap fitness" should "count subject as 7" in {
		nondecept(subject) should be (7)
	}
	"nondecipte trap fitness " should "count these infividual elements correctly " in{
		nondecept("1111") should be (4)
		nondecept("1110") should be (0)
		nondecept("1100") should be (0.5f)
		nondecept("1000") should be (1f)
		nondecept("0000") should be (1.5f)
	}

}

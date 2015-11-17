package nl.jappieklooster.hw.ec

trait IHasFitness{
	def getFitness:String
}

object Fitness {
	def uniformlyScaledCountOnes(s:IHasFitness) = s.getFitness.count(c => c == '1')

	def linearlyScaledCountOnes(s:IHasFitness) = s.getFitness.
			// make a map like thing of it where the index is the key
			zipWithIndex.
			// select the elements where 1
			filter(_._1 == '1').
			// throw away the elements and keep the indeci
			map(_._2).
			// result
			sum
	def blockValuation(block:Seq[Float])(s:IHasFitness) = s.getFitness.
			// make a map like thing of it where the index is the key
			zipWithIndex.
			// select the elements where 1
			filter(_._1 == '1').
			// throw away the elements and keep the indeci
			map(x => block.apply(x._2 % block.size)).
			// result
			sum
}

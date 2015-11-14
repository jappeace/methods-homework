package nl.jappieklooster.hw.ec

trait Valuable{
	def getValuation:String
}

object Valuation {
	def uniformlyScaledCountOnes(s:Valuable) = s.getValuation.count(c => c == '1')

	def linearlyScaledCountOnes(s:Valuable) = s.getValuation.
			// make a map like thing of it where the index is the key
			zipWithIndex.
			// select the elements where 1
			filter(_._1 == '1').
			// throw away the elements and keep the indeci
			map(_._2).
			// result
			sum
	def blockValuation(block:Seq[Float])(s:Valuable) = s.getValuation.
			// make a map like thing of it where the index is the key
			zipWithIndex.
			// select the elements where 1
			filter(_._1 == '1').
			// throw away the elements and keep the indeci
			map(x => block.apply(x._2 % block.size)).
			// result
			sum
}

package nl.jappieklooster.hw.ec

trait IHasFitness{
	def fitness:Int
}
trait FitnessEvaluator{
	private var calls = 0
	protected def getFunction:String => Int
	def valuate(x:String):Int ={
		calls += 1
		getFunction(x)
	}
	def countCalls():Int = {
		val result = calls
		calls = 0
		result
	}
}

object Fitness {
	private def whereCharIsOne(c:Char) = c == '1'
	def uniformlyScaledCountOnes(s:String) = s.count(whereCharIsOne)

	def linearlyScaledCountOnes(s:String) = s.
			// make a map like thing of it where the index is the key
			zipWithIndex.
			// select the elements where 1
			filter(_._1 == '1').
			// throw away the elements and keep the indeci
			// also increase the index value by one
			map(_._2+1).
			// result
			sum
	def blockValuation(block:Seq[Float])(s:String) = s.
			// make blocks of the string
			grouped(block.size-1).
			map(
				// get the value specified in the block
				str => block.apply(str.count(whereCharIsOne))
			).sum.toInt
	def createProbe(f:String=>Int):FitnessEvaluator = new FitnessEvaluator {
		protected override def getFunction: (String) => Int = f
	}
}

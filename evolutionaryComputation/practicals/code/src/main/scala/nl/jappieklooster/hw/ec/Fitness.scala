package nl.jappieklooster.hw.ec

trait IHasFitness{
	def fitness:Float
}
trait FitnessEvaluator{
	private var calls = 0
	protected def getFunction:String => Float
	def valuate(x:String):Float ={
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
	def uniformlyScaledCountOnes(s:String):Float = s.count(whereCharIsOne)

	def linearlyScaledCountOnes(s:String):Float = s.
			// make a map like thing of it where the index is the key
			zipWithIndex.
			// select the elements where 1
			filter(_._1 == '1').
			// throw away the elements and keep the indeci
			// also increase the index value by one
			map(_._2+1).
			// result
			sum
	def blockValuation(block:Seq[Float])(s:String):Float = {
		val size = block.size-1
		// make blocks of the string
		s.grouped(size).map(
			// get the value specified in the block
			str => block(size - uniformlyScaledCountOnes(str).toInt)
		).sum
	}
	def createProbe(f:String=>Float):FitnessEvaluator = new FitnessEvaluator {
		protected override def getFunction: (String) => Float = f
	}
}

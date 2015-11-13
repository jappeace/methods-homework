package nl.jappieklooster.hw.ec

object Main{

	def main(args:Array[String]){
		println("scala!")
		val population = Population.createOneZeros(20,40)
		for(member <- population.members){
			println(member)
			println("uniform:" + Valuation.uniformlyScaledCountOnes(member))
			println("linear:" + Valuation.linearlyScaledCountOnes(member))
			println("trap:" + Valuation.blockValuation(List(3,2,1,4))(member))

			println("--")
		}
	}
}

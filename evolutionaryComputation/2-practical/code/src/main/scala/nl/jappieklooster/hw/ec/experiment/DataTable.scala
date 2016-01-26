package nl.jappieklooster.hw.ec.experiment

import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.exception.MathIllegalArgumentException
import org.apache.commons.math3.stat.descriptive.SummaryStatistics


object DataTable {

	def createTable(tableRow: TableRow*): String ={
		val toprow = "zero    name        firsty      secondy     errbotstart     errbotend    errtopstart    errtopend"
		tableRow.foldLeft(toprow)((a,b)=>a+System.lineSeparator() +b.toString)
	}

	def createRow(name:String, data:Seq[Double]):TableRow ={
		// Build summary statistics of the dataset "data"
		val stats = new SummaryStatistics()
		data.foreach(stats.addValue)

		def calcMeanCI(confidence:Double) = {
			// Create T Distribution with N-1 degrees of freedom
			val tDist = new TDistribution(stats.getN - 1)
			// Calculate critical value
			val critVal = tDist.inverseCumulativeProbability(1.0 - (1 - confidence) / 2)
			// Calculate confidence interval
			critVal * stats.getStandardDeviation / Math.sqrt(stats.getN)
		}

		// Calculate 95% confidence interval
		val ci = calcMeanCI(0.95)
		val lower = stats.getMean - ci
		val upper = stats.getMean + ci
		val sorted = data.sorted
		TableRow(name, lower, upper,sorted.head-lower,sorted.last - upper)
	}
}

case class TableRow(name:String, firstY:Double, secondY:Double, firstErr:Double, secondErr:Double){
	override def toString:String = s"0	$name	$firstY		$secondY	0 	$firstErr	0	$secondErr"
}

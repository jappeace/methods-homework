package nl.jappieklooster.hw.ec.experiment

import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.exception.MathIllegalArgumentException
import org.apache.commons.math3.stat.descriptive.SummaryStatistics
import org.apache.commons.math3.stat.inference.TTest


object DataTable {

	val significance = 0.95D
	val br = System.lineSeparator()
	def toPgfRow(row:TableRow) = s"0	${row.name}	${row.firstY}	${row.secondY-row.firstY}	${row.firstErr} 0		0	${row.secondErr}"
	def createPgfPlotTable(tableRow: TableRow*): String ={
		val toprow = "zero    name        firsty      secondy     errbotstart     errbotend    errtopstart    errtopend"
		tableRow.foldLeft(toprow)((a,b)=>a+ br +toPgfRow(b))
	}

	def createLatexResultTable(tableRow: TableRow*): String ={
		val toprow = s"\\begin{tabular}{lllll}$br name & low & high & error bottom & error top \\\\ \\toprule"

		tableRow.foldLeft(toprow)((a,b)=> a + br +
		  f"${b.name}%s & ${b.firstY}%4.2f & ${b.secondY}%4.2f & ${b.firstErr}%4.2f & ${b.secondErr}%4.2f \\\\") +
		"\\\\ \\bottomrule" + br + "\\end{tabular}"
	}
	def createLatexTTestTable(tableRow: TableRow*): String ={

		val toprow = s"\\begin{tabular}{${
			tableRow.foldLeft("l")((a,b)=> a+"l")
		}}$br tTests & ${
			val r = tableRow.foldLeft("")((a,b)=>s"$a \\rotatebox[origin=br]{-90}{${b.name}} &")
			r.substring(0,r.length-1)
		} \\\\ \\toprule" + br

		val tester = new TTest
		val rows = tableRow.foldLeft("")((prevRows,currentRow)=>{
			val td = " & "
			val varColls = tableRow.foldLeft(td)((prevCells,currentCell)=>{
				val cell = if(currentCell == currentRow){"x"}else{
					if(tester.tTest(currentRow.data.map(_.toDouble).toArray, currentCell.data.map(_.toDouble).toArray, 1- significance)){
						"T"
					}else{
						"F"
					}
				}
				prevCells + cell+  td
			})
			prevRows+currentRow.name+varColls.substring(0,varColls.length-td.length) + "\\\\" + br
		})
		toprow + rows + "\\end{tabular}"
	}

	def createRow(name:String, data:Seq[Float]):TableRow ={
		// Build summary statistics of the dataset "data"
		val stats = new SummaryStatistics()
		data.foreach(stats.addValue(_))

		def calcMeanCI(confidence:Double) = {
			// Create T Distribution with N-1 degrees of freedom
			val tDist = new TDistribution(stats.getN - 1)
			// Calculate critical value
			val critVal = tDist.inverseCumulativeProbability(1.0 - (1 - confidence) / 2)
			// Calculate confidence interval
			critVal * stats.getStandardDeviation / Math.sqrt(stats.getN)
		}

		// Calculate 90% confidence interval
		val ci = calcMeanCI(significance)
		val lower = (stats.getMean - ci).toFloat
		val upper = (stats.getMean + ci).toFloat
		val sorted = data.sorted
		val result = TableRow(name, lower, upper,Math.abs(sorted.head-lower),Math.abs(sorted.last - upper),data)

		result
	}
}

case class TableRow(name:String, firstY:Float, secondY:Float, firstErr:Float, secondErr:Float, data:Seq[Float]){
}

package nl.jappieklooster.hw.ec.experiment

import org.jfree.chart.plot.CategoryPlot
import org.sameersingh.scalaplot._
import org.sameersingh.scalaplot.Implicits._
import java.io.{File, PrintWriter}
import scala.util.Properties.lineSeparator
object Plot {
	val br = lineSeparator
	type GraphData = (String,Seq[Double])
	val folder = "../"
	def asciiGraph(data:GraphData) = {
		val weirdAddedChar = 12.toChar.toString
		var result = s"\\begin{verbatim} $br"
		result += output(ASCII, createPlot(data._2)).replaceAll(weirdAddedChar, "")
		result += s"\\end{verbatim} $br"
		result
	}
	def createPlot(xy: Seq[Double]) = {
		val yAxis = new NumericAxis
		yAxis.label = "succeses"
		yAxis.range_= (0.0, xy.max * 1.1)

		barChart(data = new BarData(new MemBarSeries(xy)), title = "blah")
	}
	def svgGraph(data:GraphData):String = {
		val name = data._1.replace(' ','_')
		val svgName=name+".svg"
		write(svgName, output(SVG, createPlot(data._2)))
		import sys.process._
		// try to 'compile' the new svg files, should crass gracfully
		s"inkscape -D -z --file=$folder$svgName --export-pdf=$folder$name.pdf --export-latex" !

		// we just assume the 'compiling' worked
		return  s"\\def\\svgwidth{\\columnwidth} $br"+
				s"\\input{$name.pdf_tex}$br"
	}

	/**
	 * write to file
	 * @param filename
	 * @param content
	 * @return
	 */
	def write(filename:String, content:String) = {
		val outputFile = new File(folder+filename)
		outputFile.delete()
		outputFile.createNewFile()
		new PrintWriter(outputFile){
			write(content)
			close()
		}
	}
}
case class TTestBarValue(values:Seq[Numeric], errorMarginPercent:Int){
	private val sorted = values.sorted
	val errorCount = values.length/errorMarginPercent
	val top = ErrorMargin(sorted.take(errorCount), errorCount)
	val bot = ErrorMargin(sorted.drop(errorCount), errorCount)
}
case class ErrorMargin(errorValues:Seq[Numeric], errorCount:Int){
	val mid = errorValues.sum/errorCount
	val top = errorValues.head - mid
	val bot = mid - errorValues.last
}

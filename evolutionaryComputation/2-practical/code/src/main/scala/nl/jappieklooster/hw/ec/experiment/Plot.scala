// Evolutionary Computing Analyzer, it analyzes various genetic algoritms
// Copyright (C) 2015 Jappie Klooster

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/\>.
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

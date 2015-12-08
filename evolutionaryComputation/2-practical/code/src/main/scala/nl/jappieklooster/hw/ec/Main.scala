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
package nl.jappieklooster.hw.ec

import java.io.{File, PrintWriter}

import org.sameersingh.scalaplot._
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.Random
import scala.util.Properties.lineSeparator

object Main{
	val log = LoggerFactory.getLogger(Main.getClass)
	val random = Random
	val br = lineSeparator
	val folder = "../"
	type GraphData = (String,Seq[Seq[(Double,Double)]])

	def main(args:Array[String]){
		log.info("starting graph bipartitioning")
		val verteci = Source.fromFile("src/main/resources/data.txt").getLines().zipWithIndex.map((elem) => {
			val id = elem._2 + 1
			val line = elem._1
			Vertex(id, xy._1,xy._2,Nil)
		})
		for (vertex <- verteci){
			log.info(vertex.toString)
		}
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

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

import nl.jappieklooster.hw.ec.algorithm.{LocalSearch, Evaluation}
import nl.jappieklooster.hw.ec.experiment.Timer
import nl.jappieklooster.hw.ec.model._
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

		def strToInts(str:String):Seq[Int] = if(str.length > 0) str.split(" ").map(
			// they have 1 based index
			_.toInt-1
		) else Nil
		val graph = Graph.create(Source.fromFile("src/main/resources/data.txt").getLines().map(strToInts).toSeq:_*)
		log.info(graph.toString)

		/*
		Implement MLS with the VSN local search. Use the first improvement version of
		the local search. Generate 1000 local optima for a single run of MLS. Measure the
		computational time required.
		 */
		val factory = MemberFactories.tightlyLinked(Evaluation.graphValuation(graph)) _
		val localSearch = new LocalSearch(LocalSearch.vertexSwapFirstImprovement(graph, factory))
		val starts = Population.createEqualOnesZeros(random, factory, graph.verteci.length, 1000)
		var timer = Timer()
		//val localOptima = starts.par.map(localSearch.search).toArray.sortBy(-_.fitness)
		//log.info(s"runtime: ${timer.timeSinceCreation}")
		//log.info("{"+localOptima.foldLeft("")((a,b) => s"$a, ${b.fitness}") + "}")

		//  1862.0, 1842.0, 1798.0, 1792.0
		/*
		 Investigate the impact of different mutation/perturbation sizes for ILS with the VSN
		 local search. Start with the smallest possible - this is, applying the vertex swap
		 operator twice. Keep investigating larger perturbation sizes until the performance of
		 ILS drops (investigate at least the perturbations that apply the vertex swap operator
		 2, 3, 4 and 5 times). Are the ILS versions statistically better/worse than MLS ?
		 Are the results obtained with the different mutation/perturbation sizes statistically
		 different from each other ?
		 */
		log.info(starts.head + "")
		for(size <- 1.to(20)){
			val iterativeLocalsearch = new LocalSearch(
				(localSearch.search _ ).compose(LocalSearch.Iterative.swapMutation(size,random,factory)),
				stopCondition = LocalSearch.StopConditions.isWorse,
				decideResult = LocalSearch.ResultDecision.previous
			)
			timer = Timer()
			log.info(s"$size: ${iterativeLocalsearch.search(starts.head)}")
			log.info(s"runtime: ${timer.timeSinceCreation}")
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

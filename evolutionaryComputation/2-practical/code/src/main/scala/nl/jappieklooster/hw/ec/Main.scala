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

import nl.jappieklooster.hw.ec.algorithm.Search.{RetryOnResult, Probe}
import nl.jappieklooster.hw.ec.algorithm._
import nl.jappieklooster.hw.ec.experiment.{Experiment, Timer}
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

		def strToInts(str:String):Seq[Int] = {
			if(str.length <= 30){
				return Nil
			}
			val sub = str.substring(30)
			if(sub.length > 0) sub.split(" ").map(
				// they have 1 based index
				_.toInt-1
			) else Nil
		}
		val graph = Graph.create(Source.fromFile("src/main/resources/data.txt").getLines().map(strToInts).toSeq:_*)
		def printResults(members:Seq[IMember]) = {
			val results = members.foldLeft("")((a,b) => s"$a, ${graph.edgeCount-b.fitness}").substring(2)
			log.info(s"{$results}")
		}
		log.info(graph.toString)

		/*
		Implement MLS with the VSN local search. Use the first improvement version of
		the local search. Generate 1000 local optima for a single run of MLS. Measure the
		computational time required.
		 */
		val factory = MemberFactories.tightlyLinked(Evaluation.graphValuation(graph)) _
		val localSearch = new Search(Search.vertexSwapFirstImprovement(graph, factory))
		val starts = Population.createEqualOnesZeros(factory, graph.verteci.length, 100)
		val timer = Timer()
		val localOptima = timer.time{
			starts.par.map(localSearch.search).toArray.sortBy(-_.fitness)
		}
		val expirement = new Experiment({
			starts.par.map(localSearch.search).toArray.sortBy(-_.fitness)
		}).run
		log.info(s"runtime: ${timer.seconds}")
		printResults(localOptima)


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
		//  1 fitness {2310.0,
		log.info(starts.head + "")
		for(size <- 1.to(10).par){
			val probe = new Probe
			val resultplexer = new RetryOnResult(30, null)
			import Search._
			val iterativeLocalsearch = new Search(
				(localSearch.search _ ).compose((Iterative.swapMutation(size,random,factory) _ ).compose(probe)),
				stopCondition = StopCondition.resetRetryOnBetter(resultplexer, StopCondition.isWorse),
				decideResult = resultplexer
			)
			resultplexer.method = iterativeLocalsearch.search
			timer.time{
				iterativeLocalsearch.search(starts.head)
			}
			log.info(s"size $size - runtime: ${timer.seconds}")
			val results = probe.tracked.sortBy(-_.fitness)
			printResults(results)
			log.info("--")
		}


		/*
		val popSizes = List(25,50,75)
		val selectMethods = List(
			("Kill parents", FittestFilter.killParents _),
			("Tournament", FittestFilter.tournementElitism _),
			("Truncate", FittestFilter.truncateElitism _)).foldRight(List[(String, FittestFilter.FittestFilter, Int)]())((a,b)=> {
			b ++ popSizes.map(x=>(a._1, a._2, x))
		})
		for(method <- selectMethods.par){
			val gls = new Evolution(
				MateSelection.createCompeteWithRandomTournement(random),
				OffspringGenerator.balancedUniformCross,
				method._2
			)
			val result = timer.time{
				gls.startGenetic(
					Population.createEqualOnesZeros(
						MemberFactories.withLocalSearch(
							localSearch,
							MemberFactories.tightlyLinked
						)(Evaluation.graphValuation(graph)),
						graph.verteci.length,
						method._3
					)
				)
			}

			log.info(s"GLS ${method._1} selection popsize ${method._3}")
			log.info(s"Runtime: ${timer.seconds}")
			log.info(s"GLS avg. fitness {${result.reverseMap{pop => pop.foldLeft(0f)((a,b)=>a+b.fitness)/pop.size}}}")
			log.info("---")
		}

		*/


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

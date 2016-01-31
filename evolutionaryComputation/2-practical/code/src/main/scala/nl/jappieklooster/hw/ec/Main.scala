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

import nl.jappieklooster.hw.ec.algorithm.search.{FidduciaMathesisSearch, VertexSwapFirstImprovement, Search}
import nl.jappieklooster.hw.ec.algorithm.search.Search.{StopCondition, RetryOnResult, Probe}
import nl.jappieklooster.hw.ec.algorithm._
import nl.jappieklooster.hw.ec.experiment._
import nl.jappieklooster.hw.ec.model._
import org.jfree.chart.renderer.category.IntervalBarRenderer
import org.jfree.chart.renderer.xy.XYErrorRenderer
import org.jfree.chart.{ChartFrame, ChartFactory}
import org.jfree.chart.plot.{CategoryPlot, PlotOrientation}
import org.jfree.data.category.{DefaultIntervalCategoryDataset, DefaultCategoryDataset}
import org.jfree.data.xy.{YIntervalSeriesCollection, YIntervalSeries}
import org.sameersingh.scalaplot.jfreegraph.JFGraphPlotter
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.Random


object Main{
	val log = LoggerFactory.getLogger(Main.getClass)
	val random = Random
	val folder = "../"

	val glsPopSizes = List(50)
	val mlsStartsize = 1000
	val ilsUntill = 7
	val ilsValidation = 100
	val ilsStopCondition = StopCondition.isWorseEq _

	val experimentCount = 30
	val experimentExecutionMethod = Experiment.Parralel _

	def main(args:Array[String]) {
		log.info("starting graph bipartitioning")


		def strToInts(str: String): Seq[Int] = {
			if (str.length <= 30) {
				return Nil
			}
			val sub = str.substring(30)
			if (sub.length > 0) sub.split(" ").map(
				// they have 1 based index
				_.toInt - 1
			)
			else Nil
		}
		var divesion = 2
		val graph:Graph = {
			val orignal = Graph.create(Vertex.create)(Source.fromFile("src/main/resources/data.txt").getLines().map(strToInts).toSeq: _*)
			val pruned = Graph.create(Vertex.pruned)(Source.fromFile("src/main/resources/data.txt").getLines().map(strToInts).toSeq: _*)

			val genes = OffspringGenerator.randomBalancedGenes(orignal.verteci.length)
			def createMember(graph:Graph, genes:String = genes) = MemberFactories.tightlyLinked(Evaluation.graphValuation(graph))(genes)

			// testing fancy genes
			val fancyGenes = "11100000101001111000000100110111101010000000110011100110011110101110110010111110010010010011000000101100111010011100001000111000110111110101110000010011111001110110100110110101110010101011000101000111100010111100010001100100000110001001010010101001110010111110001001010000001111011111111101101000101100000100011011101111001010010111001101101111100001011000001100100001011101000101111000101011101001001010101011011000110000110111111101101000001111000010010001111101000101000010111100101011100111100101"
			log.info(s"fancy gen3s:")
			log.info(s"${fancyGenes.count('1'==_)} and ${fancyGenes.count('0'==_)}")
			log.info(s"original member: ${orignal.edgeCount-createMember(orignal, fancyGenes).fitness} compared to ${pruned.edgeCount-createMember(pruned, fancyGenes).fitness}")
			log.info(s"Counting crossings ${Evaluation.graphValuationCountCrossings(orignal)(fancyGenes)}")
			// if its a doubly linked graph the pruned variant will have half
			// fitness of the not pruned variant
			if(createMember(orignal).fitness == createMember(pruned).fitness*2){
				log.info("choosing pruned graph")
				divesion = 1
				pruned
			}else{
				log.info("original graph")
				orignal
			}
		}
		def printResults(members: Seq[IMember]) = {
			val results = members.foldLeft("")((a, b) => s"$a, ${graph.edgeCount - b.fitness}").substring(2)
			log.info(s"{$results}")
		}
		log.info(graph.toString)

		def parseResults(results: Seq[Computation[Array[IMember]]]) = {
			results.flatMap(x => x.result).map(x => (graph.edgeCount - x.fitness)/divesion).sorted
		}
		/*
		Implement MLS with the VSN local search. Use the first improvement version of
		the local search. Generate 1000 local optima for a single run of MLS. Measure the
		computational time required.
		 */
		val factory = MemberFactories.tightlyLinked(Evaluation.graphValuation(graph)) _

		def runMLS(times: Int, localSearch: Search) = {
			log.info("mls")
			val expirement = new Experiment({
				log.info("experiment-mls")
				val starts = Population.createEqualOnesZeros(factory, graph.verteci.length, mlsStartsize)
				starts.map(x => localSearch.search(x)).toArray
			})

			val first = Timer.measure({
				expirement.run(experimentExecutionMethod)(times)
			})

			println(s"total time ${first.seconds}")
			val runtimes = first.result.map(x => x.seconds.toFloat).sorted

			val results = parseResults(first.result)
			(DataTable.createRow("MLS", runtimes), DataTable.createRow("MLS", results))

		}

		def runILS(times: Int, localSearch: Search) = {
			val starts = Population.createEqualOnesZeros(factory, graph.verteci.length, 1)
			log.info(s"ils start with: ${starts.head}")
			val results = 1.to(ilsUntill).map { x =>
				log.info(s"running $x")
				val experiment = new Experiment({
					// can't do this outside of the experiment, because the probe
					// needs to be added per experiment.
					val probe = new Probe
					val resultplexer = new RetryOnResult(ilsValidation, null)
					import Search._
					val iterativeLocalsearch = new Search(
						(localSearch.search _).compose((Iterative.swapMutation(x, random, factory) _).compose(probe)),
						stopCondition = StopCondition.resetRetryOnBetter(resultplexer, ilsStopCondition),
						decideResult = resultplexer
					)
					resultplexer.method = iterativeLocalsearch.search
					val search = iterativeLocalsearch.search(starts.head)
					Array(probe.tracked.maxBy(a => a.fitness))
				})
				val experiments = experiment.run(experimentExecutionMethod)(times)
				(x, experiments.map(x => x.seconds.toFloat), parseResults(experiments))
			}
			results.map(x => {
				def createRow(doubles: Seq[Float]) = DataTable.createRow(s"ILS-${x._1}", doubles)
				(createRow(x._2), createRow(x._3))
			})
		}

		def runGLS(times:Int, localSearch: Search) = {
			log.info("gls")
			val selectMethods = List(
				("KillParents", FittestFilter.killParents _),
				("Tournament", FittestFilter.tournementElitism _),
				("Truncate", FittestFilter.truncateElitism _)).foldRight(List[(String, FittestFilter.FittestFilter, Int)]())((a, b) => {
				b ++ glsPopSizes.map(x => (a._1, a._2, x))
			})
			selectMethods.map(method => {
				val experiment = new Experiment({
					val result = new Evolution(
						MateSelection.createCompeteWithRandomTournement(random),
						OffspringGenerator.balancedUniformCross,
						method._2
					).startGenetic(
						Population.createEqualOnesZeros(
							MemberFactories.withLocalSearch(
								localSearch,
								MemberFactories.tightlyLinked
							)(Evaluation.graphValuation(graph)),
							graph.verteci.length,
							method._3
						)
					)
					log.info(s"${method._1}-${method._3} found ${
						result.last.head.fitness
					} from ${
						result.head.head.fitness
					}")
					log.info(s"${method._1}-${method._3} best string: ${
						result.last.head
					}")
					Array(result.last.head)
				})
				val results = experiment.run(experimentExecutionMethod)(times)

				def createRow(doubles: Seq[Float]) = DataTable.createRow(s"GLS:${method._1}-${method._3}", doubles)
				(createRow(results.map(_.seconds.toFloat)), createRow(parseResults(results)))
			})
		}
		val fidducia = new Search(new FidduciaMathesisSearch(graph, factory))
		val vertfirst = new Search(new VertexSwapFirstImprovement(graph, factory))
		val result = List(runMLS(experimentCount, fidducia)) ::: runILS(experimentCount, fidducia).toList ::: runGLS(experimentCount, fidducia)
		output("fid", result)
		log.info("----")
		log.info("----")
		log.info("----")
		log.info("VERT NOW")
		log.info("----")
		log.info("----")
		log.info("----")
		output("vert", List(runMLS(experimentCount, vertfirst)) ::: runILS(experimentCount, vertfirst).toList ::: runGLS(experimentCount, vertfirst))
	}
	def output(name:String, result:List[(TableRow,TableRow)]):Unit = {
		import DataTable._
		Plot.write(name+"Runtimes.table", createPgfPlotTable(
			result.map(x => x._1): _*
		)
		)
		Plot.write(name+"Runtimes.textable",
			createLatexResultTable(result.map(_._1):_*)+ br + br +
			createLatexTTestTable(result.map(_._1):_*)
		)
		Plot.write(name+"Results.table", createPgfPlotTable(
			result.map(x => x._2): _*
		))
		Plot.write(name+"Results.textable",
			createLatexResultTable(result.map(_._2):_*)+ br+ br +
			createLatexTTestTable(result.map(_._2):_*)
		)
	}

}

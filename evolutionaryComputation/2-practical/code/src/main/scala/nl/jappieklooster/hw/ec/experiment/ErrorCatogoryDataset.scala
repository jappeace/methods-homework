package nl.jappieklooster.hw.ec.experiment

import org.jfree.data.category.{DefaultIntervalCategoryDataset, DefaultCategoryDataset}

import ErrorCatogoryDataset._
class ErrorCatogoryDataset(starts:DoubleArray, ends:DoubleArray, val errors:Array[Array[Line]]) extends DefaultIntervalCategoryDataset(starts,ends){
}
object ErrorCatogoryDataset{
	type DoubleArray = Array[Array[Double]]
	val Nothing = Line(0,0)
}
case class Line(start:Double, end:Double)

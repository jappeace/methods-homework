package nl.jappieklooster.hw.ec.experiment

import org.jfree.chart.renderer.category.IntervalBarRenderer
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.ui.RectangleEdge;
class ErroredIntervalBarRenderer extends IntervalBarRenderer{


	override def drawItem(
			g2:Graphics2D,
			state:CategoryItemRendererState,
			dataArea:Rectangle2D,
			plot:CategoryPlot,
			domainAxis:CategoryAxis,
			rangeAxis:ValueAxis,
			dataset:CategoryDataset,
			row:Int , column:Int , pass:Int) = {
		super.drawItem(g2, state,dataArea,plot,domainAxis,rangeAxis,dataset,row,column,pass)
		val line = dataset match {
			case x:ErrorCatogoryDataset => x.errors(row)(column)
			case _ => ErrorCatogoryDataset.Nothing
		}
		def calculateX = {
			val seriesCount = if (state.getVisibleSeriesCount >= 0){
				state.getVisibleSeriesCount
			} else{
				getRowCount
			}
			val bar = dataArea.getWidth * getItemMargin / (getColumnCount* (seriesCount - 1))
			val visibleRow = state.getVisibleSeriesIndex(row)
			visibleRow * (state.getBarWidth + bar) + domainAxis.getCategoryStart(column, this.getColumnCount, dataArea, plot.getDomainAxisEdge)
		}
		def translate(double: Double):Double = rangeAxis.valueToJava2D(double, dataArea, plot.getDomainAxisEdge);
		val x = calculateX
		val translated = line.copy(start = translate(line.start), end = translate(line.end))
		println(s"$x, ${translated.start}")

		val drawing  = new java.awt.geom.Line2D.Double(x,translated.start, x+10,translated.end)
		g2.draw(drawing)
	}

}

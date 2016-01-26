package nl.jappieklooster.hw.ec.experiment

object DataTable {

	def createTable(tableRow: TableRow*): String ={
		val toprow = "zero    name        firsty      secondy     errbotstart     errbotend    errtopstart    errtopend"
		tableRow.foldLeft(toprow)((a,b)=>a+System.lineSeparator() +b.toString)
	}
}

case class TableRow(name:String, firstY:Double, secondY:Double, firstErr:Double, secondErr:Double){
	override def toString:String = s"0	$name	$firstY		$secondY	$firstErr	0	0	$secondErr"
}

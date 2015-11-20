package nl.jappieklooster.hw.ec

/**
 * a trait that passes all behavior onto an attribute (baseSeq)
 * @tparam T
 */
trait EmulateSeq[+T] extends Seq[T] {
	def baseSeq:Seq[T]
	override def length: Int = baseSeq.length
	override def iterator: Iterator[T] = baseSeq.iterator
	override def apply(indx:Int):T = baseSeq.apply(indx)
}

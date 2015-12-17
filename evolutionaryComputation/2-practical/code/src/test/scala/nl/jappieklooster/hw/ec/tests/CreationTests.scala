package nl.jappieklooster.hw.ec.tests

import nl.jappieklooster.hw.ec.model.MemberFactories
import org.scalatest.{Matchers, FlatSpec}
import nl.jappieklooster.hw.ec.model.Population._

class CreationTests extends FlatSpec with Matchers{

	"inverse mirror " should "create an equal amount of ones and zeros" in{
		inverseMirror("1001") should be ("10010110")
		inverseMirror("1101") should be ("11010010")
		inverseMirror("11101") should be ("1110100010")
	}

}

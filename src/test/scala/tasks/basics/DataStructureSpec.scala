package tasks.basics

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec

import DataStructures._

class DataStructureSpec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks {

//sortConsideringEqualValues()

  "sortConsideringEqualValues" should "return sorted list of tuples(Set[T], Int)" in {
    sortConsideringEqualValues(
      Map("a" -> 1, "b" -> 2, "c" -> 4, "d" -> 1, "e" -> 0, "f" -> 2, "g" -> 2)
    ) shouldEqual List(Set("e") -> 0, Set("a", "d") -> 1, Set("b", "f", "g") -> 2, Set("c") -> 4)

  }
}

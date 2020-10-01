package tasks.basics

import java.io.ByteArrayInputStream
import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import ControlStructuresHomework._

class ControlStructuresHomeworkSpec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks {
  "main" should "get result" in {

    parseCommand("divide 4 5") shouldEqual Right(Command.Divide(4, 5))
    parseCommand("sum 4 5") shouldEqual Right(Command.Sum(List(4, 5)))
  }
}
//   divide 4 5
// which should output "4 divided by 5 is 0.8"

//   sum 5 5 6 8.5
// which should output "the sum of 5 5 6 8.5 is 24.5"

//   average 4 3 8.5 4
// which should output "the average of 4 3 8.5 4 is 4.875"

//   min 4 -3 -17
// which should output "the minimum of 4 -3 -17 is -17"

//   max 4 -3 -17
// which should output "the maximum of 4 -3 -17 is 4"

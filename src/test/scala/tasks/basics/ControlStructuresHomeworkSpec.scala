package tasks.basics

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import ControlStructuresHomework._

class ControlStructuresHomeworkSpec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks {

  "main" should "get result" in {
    parseCommand("divide 4 5") shouldEqual Right(Command.Divide(4, 5))
    parseCommand("sum 4 5") shouldEqual Right(Command.Sum(List(4, 5)))
  }

  "process" should "get formatted result" in {
    process("divide 4 5") shouldEqual "4.0 divided by 5.0 is 0.8"
    process("sum 4 5") shouldEqual "sum of 4.0 5.0 is 9.0"
    process("min 4 5") shouldEqual "minimum of 4.0 5.0 is 4.0"
    process("max 4 5") shouldEqual "maximum of 4.0 5.0 is 5.0"
    process("average 4 5") shouldEqual "average of 4.0 5.0 is 4.5"
    process("sum ") shouldEqual "Error: No numbers provided"
  }
  "renderResult" should "format output to string" in {
    renderResult(Result("divide", List(10.0, 5.0), 2)) shouldEqual "10.0 divided by 5.0 is 2.0"
    renderResult(Result("sum", List(10.0, 5.0), 15.0)) shouldEqual "sum of 10.0 5.0 is 15.0"
  }
  "parseCommand" should "return command" in {
    parseCommand("divide 8 8") shouldEqual Right(Command.Divide(8, 8))
    parseCommand("sum 8 8") shouldEqual Right(Command.Sum(List(8, 8)))
    parseCommand("max 8 8 9") shouldEqual Right(Command.Max(List(8, 8, 9)))
    parseCommand("min 8 8 9") shouldEqual Right(Command.Min(List(8, 8, 9)))
    parseCommand("average 8 8 9") shouldEqual Right(Command.Average(List(8, 8, 9)))
    parseCommand("average 9 h") shouldEqual Left(ErrorMessage("No numbers provided"))
    parseCommand("average") shouldEqual Left(ErrorMessage("No numbers provided"))
  }
}

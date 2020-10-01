package tasks.basics

import scala.io.Source
//import cats.implicits._
import scala.util.{Failure, Success, Try}

object ControlStructuresHomework {
  // Homework

  // Create a command line application that reads various "commands" from the
  // stdin, evaluates them, and writes output to stdout.

  // Commands are:

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

  // In case of commands that cannot be parsed or calculations that cannot be performed,
  // output a single line starting with "Error: "

  sealed trait Command {
    def res: Either[ErrorMessage, Result]
    def function: String
  }

  object Command {

    final case class Divide(dividend: Double, divisor: Double) extends Command {
      override def function: String = "divide"

      override def res: Either[ErrorMessage, Result] =
        (dividend, divisor) match {
          case (_, 0) => Left(ErrorMessage("Cant divide by 0"))
          case _      => Right(Result(function, List(dividend, divisor), dividend / divisor))
        }
    }

    final case class Sum(numbers: List[Double]) extends Command {
      override def function: String = "sum"

      override def res: Either[ErrorMessage, Result] =
        Right(Result(function, numbers, numbers.sum))
    }

    final case class Average(numbers: List[Double]) extends Command {
      override def function: String = "average"

      override def res: Either[ErrorMessage, Result] =
        Right(Result(function, numbers, numbers.sum / numbers.length))
    }

    final case class Min(numbers: List[Double]) extends Command {
      override def function: String = "minimum"

      override def res: Either[ErrorMessage, Result] =
        Right(Result(function, numbers, numbers.min))
    }

    final case class Max(numbers: List[Double]) extends Command {
      override def function: String = "maxinum"

      override def res: Either[ErrorMessage, Result] =
        Right(Result(function, numbers, numbers.max))
    }
  }

  final case class ErrorMessage(value: String)
  final case class Result(method: String, input: List[Double], value: Double)

  def parseCommand(x: String): Either[ErrorMessage, Command] = {

    val line = x.split(" +")
    val method = line.head.toLowerCase
    val number = line.tail.toList
    val numbers: List[Double] = Try(number.map(_.toDouble)).getOrElse(Nil)
    if (numbers != Nil) {
      method match {
        case "divide" if numbers.length == 2 => Right(Command.Divide(numbers(0), numbers(1)))
        case "sum"                           => Right(Command.Sum(numbers))
        case "average"                       => Right(Command.Average(numbers))
        case "min"                           => Right(Command.Min(numbers))
        case "max"                           => Right(Command.Max(numbers))
        case _                               => Left(ErrorMessage("Invalid method"))
      }
    } else { Left(ErrorMessage("No numbers provided")) }

  }

  def calculate(x: Command): Either[ErrorMessage, Result] = {
    x.res
  }

  def renderResult(x: Result): String = {
    x.method match {
      case "divide" => s"${x.input(0)} divided by ${x.input(1)} is ${x.value}"
      case _        => s"${x.method} of ${x.input.mkString(" ")} is ${x.value}"
    }

  }

  def process(x: String): String = {
    val result = for {
      command <- parseCommand(x)
      res     <- calculate(command)
    } yield res

    result match {
      case Right(res) => renderResult(res)
      case Left(err)  => f"Error: ${err.value}"
    }
  }

  def main(args: Array[String]): Unit = Source.stdin.getLines() map process foreach println
}

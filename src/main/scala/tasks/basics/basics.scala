package tasks.basics

import java.util.Scanner

import scala.annotation.tailrec

object basics {
  def main(args: Array[String]): Unit = { while (true) println(combined) }

  /**
    * Option can be substituted with Either
    */
//  def gcd(a: Int, b: Int): Either[String, Int] =
//    if (a==0 && b==0) Left("Error! Invalid input, can't be (0 0 _) for gcd")
//    else (if (b == 0) Right(a.abs) else gcd(b, a % b))
//
//  def lcm(a: Int, b: Int): Either[String, Int] = {
//    if (a == 0 || b == 0) Left("Error! Invalid input, can't be (0 _ _) for gcd")
//    else Right(((a * b) / gcd(a, b).getOrElse(1)).abs)
//  }

  /** Returns Greatest Common Divisor of two integers.
    * Input can be negative (but not (0 0 _), nevertheless positive value will be returned.
    */
  @tailrec
  def gcd(a: Int, b: Int): Option[Int] = {
    if (a == 0 && b == 0) None
    else if (b == 0) Some(a.abs)
    else gcd(b, a % b)
  }

  /** Returns Least Common Multiple of two integers.
    * Input can be negative, nevertheless positive value will be returned.
    */
  def lcm(a: Int, b: Int): Option[Int] = {
    if (a == 0 || b == 0) None
    else Some(((a * b) / gcd(a, b).get).abs)
  }

  /**
    * Enter two integers and method to get Least Common Multiple (method = lcm)
    * or Greatest Common divisor (method = gcd);
    * input in form 'Int Int method' (example 2 4 lcm)
    */
  def combined: String = {

    try {
      val input: Scanner = new Scanner(System.in)
      val line = input.nextLine.split(" ")
      val a0: String = line(0)

      if (a0 == "Quit") sys.exit()
      if (line.length > 3) return "Error! Too many parameters"

      val a: Int = Integer.parseInt(line(0))
      val b: Int = Integer.parseInt(line(1))
      val method: String = line(2)

      if (method == "gcd")
        gcd(a, b)
          .getOrElse("Error! Invalid input, can't be (0 0 _) for gcd")
          .toString
      else if (method == "lcm")
        lcm(a, b)
          .getOrElse("Error! Invalid input, can't be (0 _ _) for lcm")
          .toString
      else "Error! Invalid method"
    } catch {
      case e: ArrayIndexOutOfBoundsException => "Error! Not enough parameters"
      case e: NumberFormatException          => "Error! Invalid input, must be (Int Int String)"
    }
  }
}

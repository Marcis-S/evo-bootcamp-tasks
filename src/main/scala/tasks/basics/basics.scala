package tasks.basics
import java.io.ByteArrayInputStream
import java.util.Scanner

object basics {
  def main(args: Array[String]): Unit = {

  }

  /** Returns Greatest Common Divisor of two integers.
   * Input can be negative (but not (0 0 _), nevertheless positive value will be returned.
   */
  def gcd(a: Int, b: Int): Int = {
    require(a != 0 || b != 0)
    if (b == 0) a.abs else gcd(b, a % b)
  }

  /** Returns Least Common Multiple of two integers.
   * Input can be negative, nevertheless positive value will be returned.
   */
  def lcm(a: Int, b: Int): Int = {
    require(a != 0 && b != 0)
    ((a * b) / gcd(a, b)).abs
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

      if (method == "gcd") gcd(a, b).toString
      else if (method == "lcm") lcm(a, b).toString
      else "Error! Invalid method"
    }
    catch {
      case e: ArrayIndexOutOfBoundsException => "Error! Not enough parameters"
      case e: NumberFormatException => "Error! Invalid input, must be (Int Int String)"
      case e: IllegalArgumentException => "Error! Invalid input, can't be (0 _ _) for lcm and (0 0 _) for gcd"
    }
  }
//  while (true) {
//    println(combined)
//  }
  System.setIn(new ByteArrayInputStream("0 1 gcm".getBytes))
  println(combined)
}


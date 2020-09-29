package tasks.basics
import java.io.ByteArrayInputStream
import org.scalatest._
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import flatspec._

import basics.{gcd, lcm, combined}

class basicsSpec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks{

  "gcd" should "Get greatest common deliminator of two integers" in {
    gcd(6, -3) shouldEqual Some(3)
    gcd(-8, -2) shouldEqual Some(2)
    gcd(0, 0) shouldEqual None
  }

  "lcm" should "Get lowest common multiple" in {
    lcm(3, 5) shouldEqual Some(15)
    lcm(-3, 5) shouldEqual Some(15)
    lcm(-8, -6) shouldEqual Some(24)
    lcm(0, 0) shouldEqual None
  }
  "combined" should "Get lcm or gcd" in {
    System.setIn(new ByteArrayInputStream("1 2 lcm".getBytes))
    combined shouldEqual  "2"

    System.setIn(new ByteArrayInputStream("1 2 gcd".getBytes))
    combined shouldEqual  "1"
  }

  "combined" should "Return custom input error" in {
    System.setIn(new ByteArrayInputStream("0 1 lcm 6".getBytes))
    combined shouldEqual  "Error! Too many parameters"

    System.setIn(new ByteArrayInputStream("0 1 xxx".getBytes))
    combined shouldEqual  "Error! Invalid method"

    System.setIn(new ByteArrayInputStream("0 1".getBytes))
    combined shouldEqual  "Error! Not enough parameters"

    System.setIn(new ByteArrayInputStream("0 k lcm".getBytes))
    combined shouldEqual  "Error! Invalid input, must be (Int Int String)"

    System.setIn(new ByteArrayInputStream("0 0 lcm".getBytes))
    combined shouldEqual  "Error! Invalid input, can't be (0 _ _) for lcm"

    System.setIn(new ByteArrayInputStream("0 0 gcd".getBytes))
    combined shouldEqual  "Error! Invalid input, can't be (0 0 _) for gcd"

  }

}

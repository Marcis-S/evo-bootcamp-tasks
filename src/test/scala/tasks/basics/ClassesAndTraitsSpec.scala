package tasks.basics

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import ClassesAndTraits._

class ClassesAndTraitsSpec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks {
  "move" should "Return same class" in {
    Point(6, 6).move(2, -2) shouldEqual Point(8, 4)
    Circle(5, 5, 9).move(1, 1) shouldEqual Circle(6, 6, 9)
    Sphere(5, 5, 5, 9).move(-1, -1, -1) shouldEqual Sphere(4, 4, 4, 9)
  }

}

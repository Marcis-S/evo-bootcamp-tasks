package tasks.basics

import java.io.ByteArrayOutputStream
import java.security.InvalidKeyException

import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec
import cats.effect.testing.scalatest.AsyncIOSpec
import EffectsHomework1._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util
import scala.util.{Failure, Success, Try}

class EffectsHomework1Spec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks {
  "map" should "map to IO value and return IO" in {
    for {
      exp <- IO(1)
      act <- IO(0).map(_ + 1)
    } yield assert(exp == act)

    for {
      exp <- IO("1")
      act <- IO(1).map(_.toString)
    } yield assert(exp == act)

    for {
      exp <- IO(true)
      act <- IO(1).map(_.isValidInt)
    } yield assert(exp == act)

  }
//TODO
//  "flatMap" should "flatmap to IO value and return IO" in {
//    for {
//      exp <- IO("afs")
//      act <- IO("af").flatMap(x => IO(x + "s"))
//    } yield assert(exp == act)
//  }

  "*>" should "run new IO if previous is success" in {

    val s = IO(1 / 1) *> IO(2)
    s.unsafeRunSync() shouldEqual 2

    val f = IO(1 / 0) *> IO(2)
    assertThrows[ArithmeticException](f.unsafeRunSync())

  }

  "as" should "replace IO value" in {
    for {
      exp <- IO(2)
      act <- IO("3").as(2)
    } yield assert(exp == act)
  }

  "void" should "empty IO" in {
    for {
      exp <- IO(())
      act <- IO("3").void
    } yield assert(exp == act)
  }

  "attempt" should "return IO of either" in {

    val ex = new InvalidKeyException("Exception message!")
    val l = IO.raiseError(ex).attempt
    l.unsafeRunSync() shouldEqual Left(ex)

    val r = IO("good").attempt
    r.unsafeRunSync() shouldEqual Right("good")

  }

  "option" should "return IO of option" in {

    val ex = new InvalidKeyException("Exception message!")
    val l = IO.raiseError(ex).option
    l.unsafeRunSync() shouldEqual None

    val r = IO("good").option
    r.unsafeRunSync() shouldEqual Some("good")
  }

  "handleErrorWith" should "return new IO of passed function in case of failed IO" in {

    val ex = new InvalidKeyException("Exception message!")
    val l = IO.raiseError(ex).handleErrorWith(x => IO(x.getMessage))
    l.unsafeRunSync() shouldEqual "Exception message!"

    val r = IO("good").handleErrorWith(x => IO(x.getMessage))
    r.unsafeRunSync() shouldEqual "good"
  }

  "redeem" should "handle error" in {
    val f = IO(1 / 0).redeem(_ => "Failure", _ => "Success")
    f.unsafeRunSync() shouldEqual "Failure"

    val s = IO(1 / 1).redeem(_ => "Failure", _ => "Success")
    s.unsafeRunSync() shouldEqual "Success"
  }

  "redeemWith" should "handle error" in {
    val f = IO(1 / 0).redeemWith(_ => IO("Failure"), _ => IO("Success"))
    f.unsafeRunSync() shouldEqual "Failure"

    val s = IO(1 / 1).redeemWith(_ => IO("Failure"), _ => IO("Success"))
    s.unsafeRunSync() shouldEqual "Success"
  }

  "unsafeRunSync" should "run IO without error handling" in {

    IO(1 / 1).unsafeRunSync() shouldEqual 1

    assertThrows[ArithmeticException](IO(1 / 0).unsafeRunSync())
  }

  "unsafeFuture" should "Return future of IO" in {

    IO(1 / 1).unsafeToFuture().value shouldEqual Future(1).value

    IO(1 / 0).unsafeToFuture().value shouldEqual None
  }

  "suspend" should "delay return IO value" in {
    val io = IO.suspend(IO(1)).map(_ + 1)
    io.unsafeRunSync() shouldEqual 2
  }

  "delay" should "delay return IO value" in {
    val io = IO.delay(List(1).map(_ + 1))
    io.unsafeRunSync() shouldEqual List(2)
  }

  "pure" should "return IO" in {
    IO.pure("pure IO").unsafeRunSync() shouldEqual "pure IO"
  }

  "fromEither" should "return IO from either" in {

    val l: Either[Throwable, String] = Left(new IllegalArgumentException)
    assertThrows[IllegalArgumentException](IO.fromEither(l).unsafeRunSync())

    val r: Either[Throwable, String] = Right("success")
    IO.fromEither(r).unsafeRunSync() shouldEqual "success"
  }

  "fromOption" should "return IO from option" in {

    val n: Option[String] = None
    assertThrows[IllegalArgumentException](
      IO.fromOption(n)(new IllegalArgumentException).unsafeRunSync()
    )

    val s: Option[String] = Some("success")
    IO.fromOption(s)(new IllegalArgumentException).unsafeRunSync() shouldEqual "success"
  }

  "fromTry" should "return IO from Try" in {

    val f: Try[String] = Failure(new IllegalArgumentException)
    assertThrows[IllegalArgumentException](IO.fromTry(f).unsafeRunSync())

    val s: Try[String] = Success("success")
    IO.fromTry(s).unsafeRunSync() shouldEqual "success"
  }

  "none" should "return IO of none" in {
    val s = IO.none
    s.unsafeRunSync() shouldEqual None
  }

  "raiseError" should "throw error" in {
    assertThrows[IllegalArgumentException](
      IO.raiseError(new IllegalArgumentException).unsafeRunSync()
    )
  }

  "raiseUnless" should "raise error unless statement is true" in {
    IO.raiseUnless(1 == 1)(new IllegalArgumentException).unsafeRunSync() shouldEqual ()
    assertThrows[IllegalArgumentException](
      IO.raiseUnless(1 == 0)(new IllegalArgumentException).unsafeRunSync()
    )
  }

  "raiseWhen" should "raise error if statement is true" in {
    IO.raiseWhen(1 == 0)(new IllegalArgumentException).unsafeRunSync() shouldEqual ()
    assertThrows[IllegalArgumentException](
      IO.raiseWhen(1 == 1)(new IllegalArgumentException).unsafeRunSync()
    )
  }

  "unlessA" should "return unit IO unless condition true" in {
    val bs = new ByteArrayOutputStream()
    Console.withOut(bs) {
      val f = IO.unlessA(1 == 0)(IO(print("success"))).unsafeRunSync()
      bs.toString shouldEqual "success"
    }

    Console.withOut(bs) {
      val f = IO.unlessA(1 == 1)(IO(println("success")))
      f.unsafeRunSync() shouldEqual ()
    }

  }

  "whenA" should "return unit IO when condition true" in {
    val bs = new ByteArrayOutputStream()
    Console.withOut(bs) {
      val f = IO.whenA(1 == 1)(IO(print("success"))).unsafeRunSync()
      bs.toString shouldEqual "success"
    }

    Console.withOut(bs) {
      val f = IO.whenA(1 == 0)(IO(println("success")))
      f.unsafeRunSync() shouldEqual ()
    }

  }

}

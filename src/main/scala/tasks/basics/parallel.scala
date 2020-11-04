//package tasks.basics
//
//import cats.implicits.{catsSyntaxParallelSequence, catsSyntaxTuple2Parallel}
//
//object parallel {
//  def main(args: Array[String]): Unit = {}
//  import cats.Semigroupal
//  import cats.instances.either._
//  import cats.syntax.apply._
//  import cats.instances.vector._
//
//  /**
//    * Either as we know is nice for error-handling when we are interessed in the reason error was thrown, but as Either
//    * is monad - it has flatmap function which runs till the first error and returns it as left. In some cases where
//    * validations are independent we would like to know all the reasons for failure. Cats Validated can be used for such
//    * purpose, but if both types are needed Eithers and Validated need to be changed back and forth.
//    *
//    */
//
//  // Take this example. Combining Rights is no problem:
//  type ErrorOr[A] = Either[Vector[String], A]
//
//  val error1: ErrorOr[Int] = Left(Vector("Error 1"))
//  val error2: ErrorOr[Int] = Left(Vector("Error 2"))
//
//  val res1: ErrorOr[Int] = Right(5)
//  val res2: ErrorOr[Int] = Right(3)
//
//  List(res1, res2).parSequence
////  Output: Right((5,3))
////  As one might expect this returns tuple as a Right
//
//  (res1, error1).tupled
////  Output: Left(Vector(Error 1))
//// If one of values is left, that value is returned
//
//  (error1, error1).tupled
////  Left(Vector(Error 1))
//
//  /**
//    * But if there are multiple Lefts only first of them is returned (as monads implement flatMap). Sometimes we would
//    * like to collect all the Either errors at once and go from there so here 'parallel' comes in play offering special
//    * 'parallel' methods.
//    */
//  (error1, error1).parTupled
////  Output: Left(Vector(Error 1, Error 1))
//
////  Any type that has a Semigroup instance will work.
//  type ErrorOr1[A] = Either[List[String], A]
//  val error11: ErrorOr1[Int] = Left(List("Error 1"))
//  val error21: ErrorOr1[Int] = Left(List("Error 2"))
//
//  (error11, error21).parTupled
//// Output: Left(List(Error 1, Error 2))
//
//// Although some might be more useful than others at times:
//  type ErrorOr2[A] = Either[String, A]
//  val error12: ErrorOr2[Int] = Left("Error 1")
//  val error22: ErrorOr2[Int] = Left("Error 2")
//
//  (error12, error22).parTupled
//// Output: Left(Error 1Error 2)
//
//  /**
//    * So a bit more functional example from Cats Parallel homepage. With similar task as we had - validating
//    * person instead of credit card.
//    * Case class, helper and validation function definition:
//    */
//
//  import cats.implicits._
//
//  case class Person(name: String, age: Int)
//
//  def parse(s: String): Either[List[String], Int] = ???
//  def validateAge(a: Int): Either[List[String], Int] = ???
//  def validateName(n: String): Either[List[String], String] = ???
//
//  // Implementation of all error gathering as we did it:
//
//  def parsePerson(ageString: String, nameString: String) =
//    for {
//      age <- parse(ageString)
//      person <- (validateName(nameString).toValidated, validateAge(age).toValidated)
//        .mapN(Person)
//        .toEither
//    } yield person
//
//  // Can be turned into this, keeping all Errors and Either monad, without type conversion:
//
//  def parsePerson1(ageString: String, nameString: String) =
//    for {
//      age    <- parse(ageString)
//      person <- (validateName(nameString), validateAge(age)).parMapN(Person)
//    } yield person
//
//  /**
//    * Parallel method are accessible from import cats.syntax.parallel
//    * Example with parTraverse: https://typelevel.org/cats-effect/datatypes/io.html#partraverse
//    *
//    * Extra general info:
//    * https://typelevel.org/cats/typeclasses/parallel.html
//    * https://www.youtube.com/watch?v=Tx5LdRCaDcY
//    * Scala with Cats (book)
//    */
//}

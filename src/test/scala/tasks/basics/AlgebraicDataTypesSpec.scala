package tasks.basics

import org.scalatest._
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import flatspec._
import AlgebraicDataTypes._
import tasks.basics.AlgebraicDataTypes.Combination.RoyalFlush

class AlgebraicDataTypesSpec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks {

  val fiveCardSet: Set[Card] = Set(
    Card(Suit('c'), Rank(13)),
    Card(Suit('h'), Rank(13)),
    Card(Suit('s'), Rank(13)),
    Card(Suit('h'), Rank(11)),
    Card(Suit('h'), Rank(2))
  )
  val fourCardSet: Set[Card] = fiveCardSet.slice(0, 4)
  val twoCardSet: Set[Card] = fiveCardSet.slice(0, 2)

  val emptyCardSet: Set[Card] = Nil.toSet

  "Rank" should "get valid rank or error message" in {
    Rank.create(2) shouldEqual Right(Rank(2))
    Rank.create(14) shouldEqual Right(Rank(14))
    Rank.create(1) shouldEqual Left(ErrorMessage("Invalid rank value"))
    Rank.create(15) shouldEqual Left(ErrorMessage("Invalid rank value"))
  }

  "Suit" should "get valid Suit or error message" in {
    Suit.create('c') shouldEqual Right(Suit('c'))
    Suit.create('d') shouldEqual Right(Suit('d'))
    Suit.create('h') shouldEqual Right(Suit('h'))
    Suit.create('s') shouldEqual Right(Suit('s'))
    Suit.create('l') shouldEqual Left(ErrorMessage("Invalid suit value"))
    Suit.create(1) shouldEqual Left(ErrorMessage("Invalid suit value"))
    Suit.create('S') shouldEqual Left(ErrorMessage("Invalid suit value"))
  }
  "Card" should "contain Suit and Rank values" in {
    Card(Suit('c'), Rank(13)).rank shouldEqual Rank(13)
    Card(Suit('c'), Rank(13)).suit shouldEqual Suit('c')
  }

  "Hand" should "return valid hand for Texas or Omaha" in {

    Hand.create(fourCardSet, "omaha") shouldEqual Right(Hand(fourCardSet, "omaha"))
    Hand.create(fourCardSet, "texas") shouldEqual Left(ErrorMessage("Invalid hand"))
    Hand.create(twoCardSet, "texas") shouldEqual Right(
      Hand(twoCardSet, "texas")
    )
    Hand.create(twoCardSet, "omaha") shouldEqual Left(ErrorMessage("Invalid hand"))
    Hand.create(emptyCardSet, "omaha") shouldEqual Left(ErrorMessage("Invalid hand"))

  }
  "Board" should "return board with valid card count" in {

    Board.create(fiveCardSet) shouldEqual Right(Board(fiveCardSet))
    Board.create(fourCardSet) shouldEqual Left(ErrorMessage("Invalid board"))
    Board.create(emptyCardSet) shouldEqual Left(ErrorMessage("Empty board"))
  }

  "Combination" should "contain Combination and score values" in {
    RoyalFlush(fiveCardSet, 90000000000L).score shouldEqual 90000000000L
    RoyalFlush(fiveCardSet, 90000000000L).cards shouldEqual fiveCardSet
  }
}

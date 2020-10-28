package tasks.basics

import scala.util.Try

object AlgebraicDataTypes {
  def main(args: Array[String]): Unit = {}

  final case class ErrorMessage(value: String) { def message: String = f"Error: ${value}" }

  final case class Suit private (suitVal: Char) extends AnyVal

  object Suit {

    def create(suitVal: Char): Either[ErrorMessage, Suit] = {
      val validSuits: Set[Char] = Set('c', 'd', 'h', 's')
      if (validSuits.contains(suitVal)) Right(Suit(suitVal))
      else Left(ErrorMessage("Invalid suit value"))
    }
  }

  final case class Rank private (rankVal: Int) extends AnyVal

  object Rank {

    def create(rankVal: Int): Either[ErrorMessage, Rank] = {
      val validRanks = Set(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
      if (validRanks.contains(rankVal)) Right(Rank(rankVal))
      else Left(ErrorMessage("Invalid rank value"))
    }
  }

  final case class Card(cardSuit: Suit, cardRank: Rank) {
    def rank: Rank = cardRank
    def suit: Suit = cardSuit
  }

  final case class Hand private (handCards: Set[Card], variant: String)

  object Hand {

    def create(handCards: Set[Card], variant: String): Either[ErrorMessage, Hand] =
      variant match {
        case "omaha" if handCards.size == 4 => Right(Hand(handCards, variant))
        case "texas" if handCards.size == 2 => Right(Hand(handCards, variant))
        case _                              => Left(ErrorMessage("Invalid hand"))
      }
  }

  final case class Board private (cards: Set[Card])

  object Board {

    def create(cards: Set[Card]): Either[ErrorMessage, Board] = {
      cards.size match {
        case 5 => Right(Board(cards))
        case 0 => Left(ErrorMessage("Empty board"))
        case _ => Left(ErrorMessage("Invalid board"))
      }
    }
  }

//  Each combination is represented as vector of length 6 in form of list of
  //  integers then transformed to integral by 0 padding
//
//  hcr - high card rank; nkr - n of kind rank; hcr1 - high card,
  //  hcr2 - high card of the remaining cards excluding hc1, ect.
//
//    Royal flush [9, 0, 0, 0, 0, 0] => 90000000000
//    Straight flush [8, hcr1, 0, 0, 0, 0] => 8hcr100000000
//    Four of kind [7, 4kr, hcr1, 0, 0, 0 ] => ect
//    Full house [6, 3kr, 2kr, 0, 0, 0] => ect
//    Flush [5, hcr1, hcr2, hcr3, hcr4, hcr5] => ect
//    Straight [4, hcr1, 0, 0, 0, 0] => ect
//    Three of kind [3, 3kr, hcr1, hcr2, 0, 0] => ect
//    Two pairs [2, 2kr1, 2kr1, hcr1, 0, 0] => ect
//    Pair [1, 2kr, 0, 0, 0, 0] => ect
//    High card [0, hcr1, hcr2, hcr3, hcr4, hcr5] => ect

  sealed trait Combinations {
    def cards: Set[Card]
    def score: Long
  }

  object Combination {
    case class RoyalFlush private (cards: Set[Card], score: Long) extends Combinations
    case class StraightFlush private (cards: Set[Card], score: Long) extends Combinations
    case class FourOfKind private (cards: Set[Card], score: Long) extends Combinations
    case class FullHouse private (cards: Set[Card], score: Long) extends Combinations
    case class Flush private (cards: Set[Card], score: Long) extends Combinations
    case class Straight private (cards: Set[Card], score: Long) extends Combinations
    case class ThreeOfKind private (cards: Set[Card], score: Long) extends Combinations
    case class TwoPair private (cards: Set[Card], score: Long) extends Combinations
    case class Pair private (cards: Set[Card], score: Long) extends Combinations
    case class HighCard private (cards: Set[Card], score: Long) extends Combinations
  }
  var k = Card(Suit('c'), Rank(14))

}

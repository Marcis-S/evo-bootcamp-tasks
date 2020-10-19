package tasks.basics.errorHandling

import java.util.Calendar

import cats.data.ValidatedNec
import cats.syntax.all._

import scala.util.Try

/**
  * Created as validation for existing cards
  */

object error_handling {

  def main(args: Array[String]): Unit = {}

  final case class ErrorMessage(value: String) { def message: String = f"Error: ${value}" }
  final case class CardHolderName(name: String, surname: String)

  object CardHolderName {

    def create(nameSurname: String): Either[ErrorMessage, CardHolderName] = {
      val nameSurnameSplit = nameSurname.split(" ")
      nameSurnameSplit.length match {
        case 2          => Right(CardHolderName(nameSurnameSplit(0), nameSurnameSplit(1)))
        case x if x < 2 => Left(ErrorMessage("No name or surname provided!"))
        case x if x > 2 => Left(ErrorMessage("Too many inputs for name"))
      }
    }
  }

  final case class CardNumberType private (cNumber: String, cType: String = "")

  object CardNumberType {

    def create(cNumber: String): Either[ErrorMessage, CardNumberType] = {
      val sValue = cNumber.replace(" ", "")
      val tryParseLong = Try(cNumber.toLong).toOption
      if (tryParseLong == None) Left(ErrorMessage("Invalid card number!"))
      else
        (sValue.length, sValue(0)) match {
          case (16, '5')                         => Right(CardNumberType(cNumber, "MasterCard"))
          case (15, '3')                         => Right(CardNumberType(cNumber, "American Express"))
          case (13, '4') | (16, '4') | (19, '4') => Right(CardNumberType(cNumber, "Visa"))
          case (16, '6') | (19, '6')             => Right(CardNumberType(cNumber, "Discovery"))
          case (_, _)                            => Left(ErrorMessage("Unsupported card type!"))
        }
    }
  }

  final case class CardExpirationDate(month: String, year: String)

  object CardExpirationDate {

    def create(expDate: String): Either[ErrorMessage, CardExpirationDate] = {
      val validMonths = (1 to 12).map(x => f"${x}%02d")
      val monthYear = expDate.split("/")
      monthYear.length match {
        case 2
            if monthYear(0).length == 2
            && monthYear(1).length == 2
            && validMonths.contains(monthYear(0))
            && (Try(monthYear(1).toInt).toOption != None) =>
          Right(CardExpirationDate(monthYear(0), monthYear(1)))
        case _ => Left(ErrorMessage("Invalid card validity date!"))
      }
    }
  }

  final case class CardCcv(ccv: String)

  object CardCcv {

    def create(ccv: String): Either[ErrorMessage, CardCcv] = {
      (ccv.length) match {
        case 3 => Right(CardCcv(ccv))
        case _ => Left(ErrorMessage("Invalid CCV number!"))
      }
    }
  }

  case class PaymentCard(
    holder: CardHolderName,
    number: CardNumberType,
    valid: CardExpirationDate,
    ccv: CardCcv
  )

  sealed trait ValidationError

  object ValidationError {

// Some of these still could be avoided by adt
    final case object UnsupportedType extends ValidationError {
      override def toString = "The card holders name contains unsupported characters"
    }

    final case object ExpiredCard extends ValidationError {
      override def toString = "Card has expired"
    }

    final case object UnsupportedCard extends ValidationError {
      override def toString = "This kind of card is not supported!"
    }

    final case object InvalidCcv extends ValidationError {
      override def toString = "CCV is not number"
    }

  }

  object PaymentCardValidator {
    import ValidationError._
    import error_handling._

    type AllErrorsOr[A] = ValidatedNec[ValidationError, A]

    //Name validation could be done by ADT upon creation
    def validateName(name: CardHolderName): AllErrorsOr[CardHolderName] = {
      if (!name.name.matches("[a-zA-Z]+")) UnsupportedType.invalidNec
      else if (!name.surname.matches("[a-zA-Z]+")) UnsupportedType.invalidNec
      else name.validNec
    }

    def validateExpirationDate(expDate: CardExpirationDate): AllErrorsOr[CardExpirationDate] = {
      val cMonth: Int = expDate.month.replaceFirst("^0*", "").toInt
      val cYear: Int = expDate.year.toInt

      val now: Calendar = Calendar.getInstance()
      val nowMonth: Int = now.get(Calendar.MONTH)
      val nowYear: Int = now.get(Calendar.YEAR).toString.takeRight(2).toInt

      if ((cYear - nowYear) * 12 - (cMonth - nowMonth) > 1) expDate.validNec
      else ExpiredCard.invalidNec
    }

    def validateType(numberType: CardNumberType): AllErrorsOr[CardNumberType] = {
//      If card type is important
      val supportedCards = List("MasterCard", "Visa")
      if (supportedCards.contains(numberType.cType)) numberType.validNec
      else UnsupportedCard.invalidNec
    }

    //CCV validation could be done by ADT upon creation
    def validateCcv(ccv: CardCcv): AllErrorsOr[CardCcv] = {
      if (Try(ccv.ccv.toInt).toOption == None) InvalidCcv.invalidNec else ccv.validNec
    }

    def validate(paymentCard: PaymentCard): AllErrorsOr[PaymentCard] =
      (
        validateName(paymentCard.holder),
        validateType(paymentCard.number),
        validateExpirationDate(paymentCard.valid),
        validateCcv(paymentCard.ccv)
      ).mapN(PaymentCard)
  }

}

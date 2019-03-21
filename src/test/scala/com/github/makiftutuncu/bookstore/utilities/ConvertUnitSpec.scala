package com.github.makiftutuncu.bookstore.utilities

import cats.{Id, MonadError}
import com.github.makiftutuncu.bookstore.UnitSpec

class ConvertUnitSpec extends UnitSpec {
  "A Convert instance for List" should {
    "return empty list when given an empty list" in {
      val list     = List.empty[String]
      val expected = List.empty[Int]

      val result = Convert[Id, List].convert[String, Int](list, parseInt)

      result should haveValue[List[Int]](expected)
    }

    "return error when some items cannot be converted in given list" in {
      val list     = List("1", "2", "osman", "4")
      val expected = Errors.invalidData("osman", Some("an integer was expected"))

      val result = Convert[Id, List].convert[String, Int](list, parseInt)

      result should haveError[List[Int]](expected)
    }

    "return converted list properly when there is no conversion error" in {
      val list     = List("1", "2", "3")
      val expected = List(1, 2, 3)

      val result = Convert[Id, List].convert[String, Int](list, parseInt)

      result should haveValue[List[Int]](expected)
    }
  }

  implicit val idListConvert: Convert[Id, List] =
    new Convert[Id, List] {
      override def convert[A, B](as: List[A], converter: A => Maybe[Id, B])(implicit F: MonadError[Id, Throwable]): Maybe[Id, List[B]] =
        as.foldLeft(Maybe.value(List.empty[B])) {
          case (m @ Left(_), _)             => m
          case (Right(currentBs), currentA) => converter(currentA).map(b => currentBs :+ b)
        }
    }

  private def parseInt(s: String): Maybe[Id, Int] =
    Maybe.attemptF[Id, Int] {
      s.toInt
    } { _ =>
      Errors.invalidData(s, Some("an integer was expected"))
    }
}

package com.github.makiftutuncu.bookstore

import cats.Id
import com.github.makiftutuncu.bookstore.utilities.{BookstoreException, Maybe}
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.language.higherKinds

trait CustomMatchers {
  def haveError[V](expected: BookstoreException): Matcher[Maybe[Id, V]] = {
    case Left(actual: BookstoreException) =>
      MatchResult(
        actual == expected,
        s"Maybe did not have expected error '$expected', it had error '$actual'",
        s"Maybe did not have expected error '$expected', it had error '$actual'"
      )

    case Right(value) =>
      MatchResult(
        matches = false,
        s"Maybe did not have error '$expected', it had value '$value'",
        s"Maybe did not have error '$expected', it had value '$value'"
      )
  }

  def haveError: Matcher[Maybe[Id, _]] = {
    case Left(_) =>
      MatchResult(matches = true, "", "")

    case Right(value) =>
      MatchResult(
        matches = false,
        s"Maybe did not have error, it had value '$value'",
        s"Maybe did not have error, it had value '$value'"
      )
  }
  
  def haveValue[V](expected: V): Matcher[Maybe[Id, V]] = {
    case Left(error: BookstoreException) =>
      MatchResult(
        matches = false,
        s"Maybe did not have value '$expected', it had error '$error'",
        s"Maybe did not have value '$expected', it had error '$error'"
      )

    case Right(actual) =>
      MatchResult(
        actual == expected,
        s"Maybe did not have expected value '$expected', it had value '$actual'",
        s"Maybe did not have expected value '$expected', it had value '$actual'"
      )
  }

  def haveValue: Matcher[Maybe[Id, _]] = {
    case Left(error: BookstoreException) =>
      MatchResult(
        matches = false,
        s"Maybe did not have value, it had error '$error'",
        s"Maybe did not have value, it had error '$error'"
      )

    case Right(_) =>
      MatchResult(matches = true, "", "")
  }
}

package com.github.makiftutuncu.bookstore.utilities

import cats.{Id, MonadError}

import scala.language.higherKinds

trait ConvertInstances {
  implicit def listConvert[F[_]]: Convert[F, List] = new Convert[F, List] {
    override def convert[A, B](as: List[A], converter: A => Maybe[F, B])(implicit F: MonadError[F, Throwable]): Maybe[F, List[B]] =
      as.foldLeft(Maybe.value(List.empty[B])) {
        case (result, a) =>
          Maybe.flatMap(result) { bs =>
            Maybe.map(converter(a)) { b =>
              bs :+ b
            }
          }
      }
  }

  implicit def optionConvert[F[_]]: Convert[F, Option] = new Convert[F, Option] {
    override def convert[A, B](maybeA: Option[A], converter: A => Maybe[F, B])(implicit F: MonadError[F, Throwable]): Maybe[F, Option[B]] =
      maybeA.fold(Maybe.value(Option.empty[B])) { a =>
        Maybe.map(converter(a)) { b =>
          Option(b)
        }
      }
  }

  implicit def idConvert[F[_]]: Convert[F, Id] = new Convert[F, Id] {
    override def convert[A, B](a: Id[A], converter: A => Maybe[F, B])(implicit F: MonadError[F, Throwable]): Maybe[F, Id[B]] =
      Maybe.map(converter(a))(identity)
  }
}

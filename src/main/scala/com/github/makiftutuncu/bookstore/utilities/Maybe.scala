package com.github.makiftutuncu.bookstore.utilities

import cats.{Applicative, MonadError}

import scala.language.higherKinds
import scala.util.Try

object Maybe {
  def unit[F[_]](implicit F: Applicative[F]): Maybe[F, Unit] = F.pure(Right(()))

  def error[F[_], A](error: BookstoreException)(implicit F: Applicative[F]): Maybe[F, A] = F.pure(Left(error))

  def value[F[_], A](a: A)(implicit F: Applicative[F]): Maybe[F, A]      = F.pure(Right(a))
  def valueF[F[_], A](fa: F[A])(implicit F: Applicative[F]): Maybe[F, A] = F.map(fa)(a => Right(a))

  def attempt[F[_], A](maybe: => Maybe[F, A])(errorHandler: Throwable => BookstoreException)(implicit F: MonadError[F, Throwable]): Maybe[F, A] =
    Try(maybe).fold[Maybe[F, A]](
      t => Maybe.error(errorHandler(t)),
      v => v
    )

  def attemptF[F[_], A](action: => F[A])(errorHandler: Throwable => BookstoreException)(implicit F: MonadError[F, Throwable]): Maybe[F, A] =
    Try(action).fold[Maybe[F, A]](
      t => Maybe.error(errorHandler(t)),
      v => Maybe.valueF(v)
    )

  def flatMap[F[_], A, B](maybe: Maybe[F, A])(f: A => Maybe[F, B])(implicit F: MonadError[F, Throwable]): Maybe[F, B] =
    F.flatMap(maybe) {
      case Left(error)  => Maybe.error[F, B](error)
      case Right(value) => f(value)
    }

  def map[F[_], A, B](maybe: Maybe[F, A])(f: A => B)(implicit F: MonadError[F, Throwable]): Maybe[F, B] =
    flatMap(maybe)(a => value(f(a)))

  def fold[F[_], A, B](maybe: Maybe[F, A])(errorHandler: BookstoreException => B, f: A => B)(implicit F: Applicative[F]): F[B] =
    F.map(maybe) {
      case Left(error)  => errorHandler(error)
      case Right(value) => f(value)
    }
}

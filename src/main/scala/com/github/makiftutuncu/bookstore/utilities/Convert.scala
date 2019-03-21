package com.github.makiftutuncu.bookstore.utilities

import cats.MonadError

import scala.language.higherKinds

trait Convert[F[_], G[_]] {
  def convert[A, B](as: G[A], converter: A => Maybe[F, B])(implicit F: MonadError[F, Throwable]): Maybe[F, G[B]]
}

object Convert extends ConvertInstances {
  def apply[F[_], G[_]](implicit C: Convert[F, G]): Convert[F, G] = C
}

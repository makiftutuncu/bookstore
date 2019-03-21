package com.github.makiftutuncu.bookstore

import cats.{Id, MonadError}

import scala.annotation.tailrec

object TestMonadError extends MonadError[Id, Throwable] {
  override def raiseError[A](e: Throwable): Id[A] = throw new Exception("Test failed", e)

  override def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
    try {
      fa
    } catch {
      case t: Throwable =>
        f(t)
    }

  override def pure[A](x: A): Id[A] = x

  override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] = f(fa)

  @tailrec
  override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] =
    f(a) match {
      case Right(b)   => b
      case Left(newA) => tailRecM(newA)(f)
    }
}

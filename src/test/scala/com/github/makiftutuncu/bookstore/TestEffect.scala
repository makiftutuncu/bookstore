package com.github.makiftutuncu.bookstore

import cats.Id
import cats.effect.{Effect, ExitCase, IO, SyncIO}

object TestEffect extends Effect[Id] {
  override def runAsync[A](fa: Id[A])(cb: Either[Throwable, A] => IO[Unit]): SyncIO[Unit] = SyncIO(fa).redeem(t => cb(Left(t)), a => cb(Right(a)))

  // Needed by Finch's await methods
  override def toIO[A](fa: Id[A]): IO[A] = IO.pure[A](fa)

  // Not implemented because `toIO` is implemented
  override def async[A](k: (Either[Throwable, A] => Unit) => Unit): Id[A] = ???
  override def asyncF[A](k: (Either[Throwable, A] => Unit) => Id[Unit]): Id[A] = ???
  override def bracketCase[A, B](acquire: Id[A])(use: A => Id[B])(release: (A, ExitCase[Throwable]) => Id[Unit]): Id[B] = ???

  override def suspend[A](thunk: => Id[A]): Id[A] = thunk

  override def raiseError[A](e: Throwable): Id[A] = TestMonadError.raiseError(e)

  override def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] = TestMonadError.handleErrorWith(fa)(f)

  override def pure[A](x: A): Id[A] = TestMonadError.pure(x)

  override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] = TestMonadError.flatMap(fa)(f)

  override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] = TestMonadError.tailRecM(a)(f)
}

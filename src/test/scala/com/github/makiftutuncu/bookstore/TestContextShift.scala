package com.github.makiftutuncu.bookstore

import cats.Id
import cats.effect.ContextShift

import scala.concurrent.ExecutionContext

object TestContextShift extends ContextShift[Id] {
  override def shift: Id[Unit] = {}

  override def evalOn[A](ec: ExecutionContext)(fa: Id[A]): Id[A] = fa
}

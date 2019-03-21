package com.github.makiftutuncu.bookstore.data

import cats.effect.Effect
import com.github.makiftutuncu.bookstore.models.Model
import com.github.makiftutuncu.bookstore.utilities.{DoobieExtras, Maybe}
import com.github.makiftutuncu.bookstore.views.{CreateView, UpdateView}
import doobie.util.fragment.Fragment

import scala.language.higherKinds

abstract class DAO[F[_], M <: Model, CV <: CreateView, UV <: UpdateView](val db: Database[F])(implicit F: Effect[F]) extends DoobieExtras {
  def getAll: Maybe[F, List[M]]

  def getByName(name: String): Maybe[F, List[M]]

  def insertSql(model: M): Fragment
}

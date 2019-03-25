package com.github.makiftutuncu.bookstore.data

import cats.effect.Effect
import com.github.makiftutuncu.bookstore.models.Model
import com.github.makiftutuncu.bookstore.utilities.DoobieExtras
import com.github.makiftutuncu.bookstore.views.{CreateView, UpdateView}

import scala.language.higherKinds

abstract class DAO[F[_], M <: Model, CV <: CreateView, UV <: UpdateView](val db: Database[F])(implicit F: Effect[F]) extends DoobieExtras

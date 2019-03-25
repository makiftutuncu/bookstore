package com.github.makiftutuncu.bookstore.services

import java.util.UUID

import cats.Id
import cats.effect.Effect
import com.github.makiftutuncu.bookstore.data.AuthorDAO
import com.github.makiftutuncu.bookstore.models.Author
import com.github.makiftutuncu.bookstore.utilities.{Convert, Maybe}
import com.github.makiftutuncu.bookstore.views.{AuthorView, CreateAuthorView, UpdateAuthorView}

import scala.language.higherKinds

class AuthorService[F[_]](override val dao: AuthorDAO[F])(implicit F: Effect[F]) extends Service[F, Author, AuthorView, CreateAuthorView, UpdateAuthorView](dao) {
  def getAll: Maybe[F, List[AuthorView]] =
    Maybe.flatMap(dao.getAll) { authors =>
      Convert[F, List].convert[Author, AuthorView](authors, toView)
    }

  def getById(id: UUID): Maybe[F, AuthorView] =
    Maybe.flatMap(dao.getById(id)) { author =>
      Convert[F, Id].convert[Author, AuthorView](author, toView)
    }

  def getByName(name: String): Maybe[F, List[AuthorView]] =
    Maybe.flatMap(dao.getByName(name)) { authors =>
      Convert[F, List].convert[Author, AuthorView](authors, toView)
    }

  def create(createView: CreateAuthorView): Maybe[F, AuthorView] =
    Maybe.flatMap(dao.create(createView)) {
      toView
    }

  def update(id: UUID, updateView: UpdateAuthorView): Maybe[F, AuthorView] =
    Maybe.flatMap(dao.update(id, updateView)) {
      toView
    }

  def delete(id: UUID): Maybe[F, Unit] =
    dao.delete(id)

  def toView(author: Author): Maybe[F, AuthorView] =
    Maybe.value(
      AuthorView(
        id   = author.id,
        name = author.name
      )
    )
}

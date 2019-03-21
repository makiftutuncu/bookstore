package com.github.makiftutuncu.bookstore.controllers

import java.util.UUID

import cats.effect.Effect
import com.github.makiftutuncu.bookstore.services.BookService
import com.github.makiftutuncu.bookstore.views.{CreateBookView, UpdateBookView}
import com.twitter.finagle.http.Status
import io.circe.Json
import io.finch.Endpoint
import io.finch.circe._
import shapeless.{:+:, CNil}

import scala.language.higherKinds

class BookController[F[_]](bookService: BookService[F])(implicit F: Effect[F]) extends Controller[F] {
  import AuthorController.{base => authorsBase}
  import BookController.base

  val get: Endpoint[F, Json] =
    get(base :: paramOption[String]("name")) { maybeName: Option[String] =>
      respond(maybeName.fold(bookService.getAll)(bookService.getByName))
    }

  // TODO: Implement getting all books of an author endpoint here.

  val getByAuthorIdAndBookId: Endpoint[F, Json] =
    get(authorsBase :: path[UUID] :: base :: path[UUID]) { (authorId: UUID, bookId: UUID) =>
      respond(bookService.getByAuthorIdAndBookId(authorId, bookId))
    }

  val create: Endpoint[F, Json] =
    post(authorsBase :: path[UUID] :: base :: jsonBody[CreateBookView]) { (authorId: UUID, createBookView: CreateBookView) =>
      respond(bookService.create(authorId, createBookView), Status.Created)
    }

  val update: Endpoint[F, Json] =
    put(authorsBase :: path[UUID] :: base :: path[UUID] :: jsonBody[UpdateBookView]) { (authorId: UUID, bookId: UUID, updateBookView: UpdateBookView) =>
      respond(bookService.update(authorId, bookId, updateBookView))
    }

  val delete: Endpoint[F, Json] =
    delete(authorsBase :: path[UUID] :: base :: path[UUID]) { (authorId: UUID, bookId: UUID) =>
      respondEmpty(bookService.delete(authorId, bookId))
    }

  val api: Endpoint[F, Json :+: Json :+: Json :+: Json :+: Json :+: CNil] =
    handlingErrors(get) :+:
    handlingErrors(getByAuthorIdAndBookId) :+:
    handlingErrors(create) :+:
    handlingErrors(update) :+:
    handlingErrors(delete)
}

object BookController {
  val base: String = "books"
}

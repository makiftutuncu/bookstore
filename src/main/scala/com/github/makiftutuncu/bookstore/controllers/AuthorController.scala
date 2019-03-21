package com.github.makiftutuncu.bookstore.controllers

import java.util.UUID

import cats.effect.Effect
import com.github.makiftutuncu.bookstore.services.AuthorService
import com.github.makiftutuncu.bookstore.views.{CreateAuthorView, UpdateAuthorView}
import com.twitter.finagle.http.Status
import io.circe.Json
import io.finch.Endpoint
import io.finch.circe._
import shapeless.{:+:, CNil}

import scala.language.higherKinds

class AuthorController[F[_]](authorService: AuthorService[F])(implicit F: Effect[F]) extends Controller[F] {
  import AuthorController.base

  val get: Endpoint[F, Json] =
    get(base :: paramOption[String]("name")) { maybeName: Option[String] =>
      respond(maybeName.fold(authorService.getAll)(authorService.getByName))
    }

  val getById: Endpoint[F, Json] =
    get(base :: path[UUID]) { authorId: UUID =>
      respond(authorService.getById(authorId))
    }

  val create: Endpoint[F, Json] =
    post(base :: jsonBody[CreateAuthorView]) { createAuthorView: CreateAuthorView =>
      respond(authorService.create(createAuthorView), Status.Created)
    }

  val update: Endpoint[F, Json] =
    put(base :: path[UUID] :: jsonBody[UpdateAuthorView]) { (id: UUID, updateAuthorView: UpdateAuthorView) =>
      respond(authorService.update(id, updateAuthorView))
    }

  val delete: Endpoint[F, Json] =
    delete(base :: path[UUID]) { id: UUID =>
      respondEmpty(authorService.delete(id))
    }

  val api: Endpoint[F, Json :+: Json :+: Json :+: Json :+: Json :+: CNil] =
    handlingErrors(get) :+:
    handlingErrors(getById) :+:
    handlingErrors(create) :+:
    handlingErrors(update) :+:
    handlingErrors(delete)
}

object AuthorController {
  val base: String = "authors"
}

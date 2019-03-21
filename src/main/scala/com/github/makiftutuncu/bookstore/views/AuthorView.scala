package com.github.makiftutuncu.bookstore.views

import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class AuthorView(id: UUID, name: String) extends View

object AuthorView {
  implicit val decoder: Decoder[AuthorView] = deriveDecoder[AuthorView]
  implicit val encoder: Encoder[AuthorView] = deriveEncoder[AuthorView]
}

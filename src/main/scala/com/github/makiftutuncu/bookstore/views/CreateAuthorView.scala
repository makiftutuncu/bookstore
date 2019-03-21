package com.github.makiftutuncu.bookstore.views

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class CreateAuthorView(name: String) extends CreateView

object CreateAuthorView {
  implicit val decoder: Decoder[CreateAuthorView] = deriveDecoder[CreateAuthorView]
  implicit val encoder: Encoder[CreateAuthorView] = deriveEncoder[CreateAuthorView]
}

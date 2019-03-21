package com.github.makiftutuncu.bookstore.views

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class UpdateAuthorView(name: String) extends UpdateView

object UpdateAuthorView {
  implicit val decoder: Decoder[UpdateAuthorView] = deriveDecoder[UpdateAuthorView]
  implicit val encoder: Encoder[UpdateAuthorView] = deriveEncoder[UpdateAuthorView]
}

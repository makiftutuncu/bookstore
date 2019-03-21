package com.github.makiftutuncu.bookstore.views

import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BookView(id: UUID,
                          isbn: String,
                          name: String,
                          author: String,
                          price: Int) extends View

object BookView {
  implicit val decoder: Decoder[BookView] = deriveDecoder[BookView]
  implicit val encoder: Encoder[BookView] = deriveEncoder[BookView]
}

package com.github.makiftutuncu.bookstore.views

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class CreateBookView(isbn: String,
                                name: String,
                                price: Int) extends CreateView

object CreateBookView {
  implicit val decoder: Decoder[CreateBookView] = deriveDecoder[CreateBookView]
  implicit val encoder: Encoder[CreateBookView] = deriveEncoder[CreateBookView]
}
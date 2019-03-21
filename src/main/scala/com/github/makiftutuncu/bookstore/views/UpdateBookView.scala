package com.github.makiftutuncu.bookstore.views

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class UpdateBookView(name: String,
                                isbn: String,
                                price: Int) extends UpdateView

object UpdateBookView {
  implicit val decoder: Decoder[UpdateBookView] = deriveDecoder[UpdateBookView]
  implicit val encoder: Encoder[UpdateBookView] = deriveEncoder[UpdateBookView]
}

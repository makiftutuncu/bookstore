package com.github.makiftutuncu.bookstore.utilities

import io.circe.Encoder

case class BookstoreException(code: Int,
                              error: String,
                              message: String,
                              details: Map[String, String] = Map.empty,
                              cause: Option[Throwable] = None,
                              includeStackTrace: Boolean = true) extends Exception(message, cause.orNull, true, includeStackTrace) {
  override def equals(obj: Any): Boolean =
    obj match {
      case that: BookstoreException =>
        this.code == that.code &&
        this.error == that.error &&
        this.message == that.message &&
        this.details == that.details

      case _ =>
        false
    }

  override def toString: String = BookstoreException.encoder(this).noSpaces
}

object BookstoreException {
  implicit val encoder: Encoder[BookstoreException] =
    Encoder.forProduct4("code", "error", "message", "details") { be =>
      (be.code, be.error, be.message, be.details)
    }
}

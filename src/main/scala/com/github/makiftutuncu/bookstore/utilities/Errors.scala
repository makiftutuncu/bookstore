package com.github.makiftutuncu.bookstore.utilities

import com.twitter.finagle.http.Status

object Errors {
  // 400
  def invalidData(what: String, reason: Option[String] = None): BookstoreException                    = BookstoreException(Status.BadRequest.code, "invalid-data", s"'$what' is invalid" + reason.fold("")(r => s" because $r"))
  def alreadyExists(model: String, column: String, value: String): BookstoreException                 = BookstoreException(Status.BadRequest.code, "invalid-data", s"$model with $column '$value' already exists")
  def inUse(model: String, column: String, value: String, table: String): BookstoreException          = BookstoreException(Status.BadRequest.code, "invalid-data", s"$model with $column '$value' is still referenced by '$table'")
  def required(column: String, value: String, table: String): BookstoreException                      = BookstoreException(Status.BadRequest.code, "invalid-data", s"No $column '$value' is found in '$table'")
  def unexpectedAction(action: String, expected: Int, model: String, actual: Int): BookstoreException = BookstoreException(Status.BadRequest.code, "invalid-data", s"Expected to $action $expected $model but $actual got affected")

  // 404
  def notFound(what: String, details: (String, String)*): BookstoreException = BookstoreException(Status.NotFound.code, "not-found", s"$what is not found", details = details.toMap)

  // 500
  def database(reason: String): BookstoreException       = BookstoreException(Status.InternalServerError.code, "database", reason)
  def database(throwable: Throwable): BookstoreException = BookstoreException(Status.InternalServerError.code, "database", throwable.getMessage, cause = Some(throwable))
  def unknown(reason: String): BookstoreException        = BookstoreException(Status.InternalServerError.code, "unknown",  reason)
  def unknown(throwable: Throwable): BookstoreException  = BookstoreException(Status.InternalServerError.code, "unknown",  throwable.getMessage, cause = Some(throwable))
}

package com.github.makiftutuncu.bookstore.controllers

import cats.effect.Effect
import com.github.makiftutuncu.bookstore.utilities.{BookstoreException, Errors, Maybe}
import com.twitter.finagle.http.Status
import com.typesafe.scalalogging.LazyLogging
import io.circe.{Encoder, Json}
import io.circe.syntax._
import io.finch.{Endpoint, Output, Outputs}

import scala.language.higherKinds

abstract class Controller[F[_] : Effect] extends Endpoint.Module[F] with Outputs with LazyLogging {
  def respondError(error: BookstoreException, status: Status = Status.InternalServerError): Output[Json] =
    Output.payload(error.asJson, status)

  def respond[A : Encoder](maybe: Maybe[F, A], successStatus: Status = Status.Ok): F[Output[Json]] =
    Maybe.fold(maybe)(
      error => respondError(error, Status(error.code)),
      value => Output.payload(value.asJson, successStatus)
    )

  def respondEmpty(maybe: Maybe[F, _], successStatus: Status = Status.Ok): F[Output[Json]] =
    Maybe.fold(maybe)(
      error => Output.payload(error.asJson, Status(error.code)),
      _     => Output.empty(successStatus)
    )

  protected def handlingErrors(endpoint: Endpoint[F, Json]): Endpoint[F, Json] =
    endpoint.handle {
      case throwable: Throwable =>
        logger.error(s"Failed to handle request!", throwable)
        respondError(Errors.unknown(throwable))
    }
}

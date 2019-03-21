package com.github.makiftutuncu.bookstore

import cats.effect.{ContextShift, Effect, IO}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import com.typesafe.scalalogging.LazyLogging
import io.finch.circe._
import io.finch.{Application, Bootstrap}

import scala.concurrent.ExecutionContext

object Main extends Components[IO] with LazyLogging {
  override implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  override implicit val effect: Effect[IO]             = cats.effect.IO.ioEffect

  val service: Service[Request, Response] =
    Bootstrap
      .serve[Application.Json](authorController.api)
      .serve[Application.Json](bookController.api)
      .toService

  def main(args: Array[String]): Unit = {
    logger.info("Running migrations")
    flyway.migrate()
    Await.ready(Http.server.serve(s":${config.server.port}", service))
  }
}

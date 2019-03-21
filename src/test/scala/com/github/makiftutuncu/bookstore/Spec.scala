package com.github.makiftutuncu.bookstore

import cats.effect._
import cats.{Id, MonadError}
import com.github.makiftutuncu.bookstore.data.Database
import com.github.makiftutuncu.bookstore.utilities.{BookstoreException, Maybe}
import com.twitter.finagle.http.Status
import com.typesafe.scalalogging.LazyLogging
import io.circe.Json
import io.finch._
import io.finch.circe._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

trait Spec extends WordSpec with Components[Id] with Matchers with CustomMatchers with LazyLogging {
  implicit val monadErrorId: MonadError[Id, Throwable] = TestMonadError

  override implicit val contextShift: ContextShift[Id] = TestContextShift
  override implicit val effect: Effect[Id]             = TestEffect

  val testException: BookstoreException = BookstoreException(Status.InternalServerError.code, "test-exception", "Test Exception", includeStackTrace = false)

  implicit class MaybeExtensions[A](maybe: Maybe[Id, A]) {
    def get: A = maybe.getOrElse(throw new IllegalArgumentException(s"Maybe $maybe did not have value!"))
  }
}

trait UnitSpec extends Spec with MockitoSugar

trait SpecWithDB extends Spec {
  lazy val failingDatabase: Database[Id] = new FailingDatabase(testException)

  logger.info("Running migrations for tests")
  flyway.migrate()
}

trait IntSpec extends SpecWithDB

trait FunSpec extends SpecWithDB {
  def getRequest(endpoint: Endpoint[Id, Json], path: String, params: Map[String, String] = Map.empty, status: Status = Status.Ok, needBody: Boolean = true): Option[Json] = {
    val response = request(endpoint, Input.get(path, params.toList: _*), status)

    if (needBody) {
      val maybeBody = response.awaitValueUnsafe()

      maybeBody.isDefined shouldBe true

      maybeBody
    } else {
      None
    }
  }

  def postRequest(endpoint: Endpoint[Id, Json], path: String, payload: Json, status: Status = Status.Ok, needBody: Boolean = true): Option[Json] = {
    val response = request(endpoint, Input.post(path).withBody[Application.Json](payload), status)

    if (needBody) {
      val maybeBody = response.awaitValueUnsafe()

      maybeBody.isDefined shouldBe true

      maybeBody
    } else {
      None
    }
  }

  def putRequest(endpoint: Endpoint[Id, Json], path: String, payload: Json, status: Status = Status.Ok, needBody: Boolean = true): Option[Json] = {
    val response = request(endpoint, Input.put(path).withBody[Application.Json](payload), status)

    if (needBody) {
      val maybeBody = response.awaitValueUnsafe()

      maybeBody.isDefined shouldBe true

      maybeBody
    } else {
      None
    }
  }

  def deleteRequest(endpoint: Endpoint[Id, Json], path: String, status: Status = Status.Ok, needBody: Boolean = true): Option[Json] = {
    val response = request(endpoint, Input.delete(path), status)

    if (needBody) {
      val maybeBody = response.awaitValueUnsafe()

      maybeBody.isDefined shouldBe true

      maybeBody
    } else {
      None
    }
  }

  private def request(endpoint: Endpoint[Id, Json], input: Input, status: Status = Status.Ok): Endpoint.Result[Id, Json] = {
    val response = endpoint(input)
    val output   = response.awaitOutputUnsafe().get

    output.status shouldBe status

    response
  }
}

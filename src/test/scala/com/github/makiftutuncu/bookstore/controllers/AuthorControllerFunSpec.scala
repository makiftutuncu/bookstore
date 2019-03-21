package com.github.makiftutuncu.bookstore.controllers

import java.util.UUID

import com.github.makiftutuncu.bookstore.models.Author
import com.github.makiftutuncu.bookstore.utilities.Errors
import com.github.makiftutuncu.bookstore.views.{AuthorView, CreateAuthorView, UpdateAuthorView}
import com.github.makiftutuncu.bookstore.{FunSpec, TestData}
import com.twitter.finagle.http.Status
import io.circe.syntax._
import org.scalatest.BeforeAndAfterEach

class AuthorControllerFunSpec extends FunSpec with TestData with BeforeAndAfterEach {
  override protected def beforeEach(): Unit = {
    super.beforeEach()
    TestAuthors.insertAll()
    TestBooks.insertAll()
  }

  override protected def afterEach(): Unit = {
    TestBooks.truncate()
    TestAuthors.truncate()
    super.afterEach()
  }

  "Getting authors" should {
    "return all authors when no name is specified" in {
      val expected = TestAuthors.allAuthorViews.asJson

      val body = getRequest(authorController.get, "/authors")

      body shouldBe Some(expected)
    }

    "return authors matching given name when a name is specified" in {
      val author   = TestAuthors.mehmetAkifTutuncu
      val expected = List(author.toView).asJson

      val body = getRequest(authorController.get, "/authors", Map("name" -> "Akif"))

      body shouldBe Some(expected)
    }
  }

  "Getting an author by id" should {
    "return not found error when author doesn't exist" in {
      val id    = UUID.randomUUID
      val error = Errors.notFound("Author", "id" -> id.toString)

      val body = getRequest(authorController.getById, s"/authors/$id", status = Status.NotFound)

      body shouldBe Some(error.asJson)
    }

    "return author with given id" in {
      val author   = TestAuthors.mehmetAkifTutuncu
      val expected = author.toView.asJson

      val body = getRequest(authorController.getById, s"/authors/${author.id}")

      body shouldBe Some(expected)
    }
  }

  "Creating a new author" should {
    "return already exists error when given author name already exists" in {
      val payload = CreateAuthorView(TestAuthors.mehmetAkifTutuncu.name)
      val error   = Errors.alreadyExists("Author", "name", TestAuthors.mehmetAkifTutuncu.name)

      val body = postRequest(authorController.create, "/authors", payload.asJson, Status.fromCode(error.code))

      body shouldBe Some(error.asJson)
    }

    "create a new author and return the created author" in {
      val payload = CreateAuthorView("Test")

      val body = {
        val maybeBody = postRequest(authorController.create, "/authors", payload.asJson, Status.Created)
        maybeBody.isDefined shouldBe true
        maybeBody.get
      }

      val authorView = {
        val maybe = body.as[AuthorView]
        maybe.isRight shouldBe true
        maybe.right.get
      }

      authorView.name shouldBe payload.name

      authorDAO.getById(authorView.id) should haveValue
    }
  }

  "Updating an author" should {
    "return unexpected action error when the author is not found" in {
      val id      = UUID.randomUUID
      val payload = UpdateAuthorView("Test")
      val error   = Errors.unexpectedAction("update", 1, "author", 0)

      val body = putRequest(authorController.update, s"/authors/$id", payload.asJson, Status.fromCode(error.code))

      body shouldBe Some(error.asJson)
    }

    "return already exists error when given author name already exists" in {
      val author  = TestAuthors.mehmetAkifTutuncu
      val payload = UpdateAuthorView(TestAuthors.sabahattinAli.name)
      val error   = Errors.alreadyExists("Author", "name", TestAuthors.sabahattinAli.name)

      val body = putRequest(authorController.update, s"/authors/${author.id}", payload.asJson, Status.fromCode(error.code))

      body shouldBe Some(error.asJson)

      val existingAuthor = {
        val maybe = authorDAO.getById(author.id)
        maybe should haveValue
        maybe.get
      }

      existingAuthor.name should not be TestAuthors.sabahattinAli.name
    }

    "update the author and return updated author" in {
      val author  = TestAuthors.mehmetAkifTutuncu
      val payload = UpdateAuthorView("Test")

      val body = {
        val maybe = putRequest(authorController.update, s"/authors/${author.id}", payload.asJson)
        maybe.isDefined shouldBe true
        maybe.get
      }

      val authorView = {
        val maybe = body.as[AuthorView]
        maybe.isRight shouldBe true
        maybe.right.get
      }

      authorView.id   shouldBe author.id
      authorView.name shouldBe payload.name

      val updatedAuthor = {
        val maybe = authorDAO.getById(author.id)
        maybe should haveValue
        maybe.get
      }

      updatedAuthor.name shouldBe authorView.name
    }
  }

  "Deleting an author" should {
    "return unexpected action error when the author is not found" in {
      val id    = UUID.randomUUID
      val error = Errors.unexpectedAction("delete", 1, "author", 0)

      val body = deleteRequest(authorController.delete, s"/authors/$id", Status.fromCode(error.code))

      body shouldBe Some(error.asJson)
    }

    "return in use error when the author still has books" in {
      val id    = TestAuthors.mehmetAkifTutuncu.id
      val error = Errors.inUse("Author", "id", id.toString, "books")

      val body = deleteRequest(authorController.delete, s"/authors/$id", Status.fromCode(error.code))

      body shouldBe Some(error.asJson)
    }

    "delete the author and return nothing" in {
      TestBooks.delete(TestBooks.test1)
      TestBooks.delete(TestBooks.test2)

      val id = TestAuthors.mehmetAkifTutuncu.id

      deleteRequest(authorController.delete, s"/authors/$id", needBody = false) shouldBe None

      authorDAO.getById(id) should haveError[Author](Errors.notFound("Author", "id" -> id.toString))
    }
  }
}

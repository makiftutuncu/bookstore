package com.github.makiftutuncu.bookstore.controllers

import java.util.UUID

import com.github.makiftutuncu.bookstore.models.Book
import com.github.makiftutuncu.bookstore.utilities.Errors
import com.github.makiftutuncu.bookstore.views._
import com.github.makiftutuncu.bookstore.{FunSpec, TestData}
import com.twitter.finagle.http.Status
import io.circe.syntax._
import org.scalatest.BeforeAndAfterEach

class BookControllerFunSpec extends FunSpec with TestData with BeforeAndAfterEach {
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

  "Getting books" should {
    "return all books when no name is specified" in {
      val expected = TestBooks.allBookViews

      val body = {
        val maybe = getRequest(bookController.get, "/books")
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe expected.asJson
    }

    "return books matching given name when a name is specified" in {
      val author   = TestAuthors.mehmetAkifTutuncu
      val expected = List(TestBooks.test1.toView(author.name), TestBooks.test2.toView(author.name))

      val body = {
        val maybe = getRequest(bookController.get, "/books", Map("name" -> "Test"))
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe expected.asJson
    }
  }

  "Getting a book by author id and book id" should {
    "return not found error when author doesn't exist" in {
      val authorId = UUID.randomUUID
      val bookId   = TestBooks.icimizdekiSeytan.id
      val error    = Errors.notFound("Book", "authorId" -> authorId.toString, "bookId" -> bookId.toString)

      val body = {
        val maybe = getRequest(bookController.getByAuthorIdAndBookId, s"/authors/$authorId/books/$bookId", status = Status.fromCode(error.code))
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe error.asJson
    }

    "return not found error when book doesn't exist" in {
      val authorId = TestAuthors.mehmetAkifTutuncu.id
      val bookId   = UUID.randomUUID
      val error    = Errors.notFound("Book", "authorId" -> authorId.toString, "bookId" -> bookId.toString)

      val body = {
        val maybe = getRequest(bookController.getByAuthorIdAndBookId, s"/authors/$authorId/books/$bookId", status = Status.fromCode(error.code))
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe error.asJson
    }

    "return not found error when book doesn't belong to given author" in {
      val authorId = TestAuthors.mehmetAkifTutuncu.id
      val bookId   = TestBooks.icimizdekiSeytan.id
      val error    = Errors.notFound("Book", "authorId" -> authorId.toString, "bookId" -> bookId.toString)

      val body = {
        val maybe = getRequest(bookController.getByAuthorIdAndBookId, s"/authors/$authorId/books/$bookId", status = Status.fromCode(error.code))
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe error.asJson
    }

    "return book with given id" in {
      val author   = TestAuthors.sabahattinAli
      val book     = TestBooks.icimizdekiSeytan
      val expected = book.toView(author.name)

      val body = {
        val maybe = getRequest(bookController.getByAuthorIdAndBookId, s"/authors/${author.id}/books/${book.id}")
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe expected.asJson
    }
  }

  "Creating a new book" should {
    "return required error when given author doesn't exist" in {
      val authorId = UUID.randomUUID
      val payload  = CreateBookView("ISBN5", "Test", 100)
      val error    = Errors.required("author_id", authorId.toString, "authors")

      val body = {
        val maybe = postRequest(bookController.create, s"/authors/$authorId/books", payload.asJson, Status.fromCode(error.code))
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe error.asJson
    }

    "return already exists error when given book isbn already exists" in {
      val authorId = TestAuthors.mehmetAkifTutuncu.id
      val payload  = CreateBookView(TestBooks.test1.isbn, "Test", 100)
      val error    = Errors.alreadyExists("Book", "isbn", TestBooks.test1.isbn)

      val body = {
        val maybe = postRequest(bookController.create, s"/authors/$authorId/books", payload.asJson, Status.fromCode(error.code))
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe error.asJson
    }

    "create a new book and return the created book" in {
      val author  = TestAuthors.mehmetAkifTutuncu
      val payload = CreateBookView("ISBN5", "Test", 100)

      val body = {
        val maybeBody = postRequest(bookController.create, s"/authors/${author.id}/books", payload.asJson, Status.Created)
        maybeBody.isDefined shouldBe true
        maybeBody.get
      }

      val bookView = {
        val maybe = body.as[BookView]
        maybe.isRight shouldBe true
        maybe.right.get
      }

      bookView.isbn   shouldBe payload.isbn
      bookView.name   shouldBe payload.name
      bookView.author shouldBe author.name
      bookView.price  shouldBe payload.price

      val createdBook = {
        val maybe = bookDAO.getByAuthorIdAndBookId(author.id, bookView.id)
        maybe should haveValue
        maybe.right.get
      }

      createdBook.isbn     shouldBe bookView.isbn
      createdBook.name     shouldBe bookView.name
      createdBook.authorId shouldBe author.id
      createdBook.price    shouldBe bookView.price
    }
  }

  "Updating a book" should {
    "return unexpected action error when the author is not found" in {
      val authorId = UUID.randomUUID
      val bookId   = TestBooks.icimizdekiSeytan.id
      val payload  = UpdateBookView("", "", 1)
      val error    = Errors.unexpectedAction("update", 1, "book", 0)

      val body = {
        val maybe = putRequest(bookController.update, s"/authors/$authorId/books/$bookId", payload.asJson, Status.fromCode(error.code))
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe error.asJson
    }

    "return unexpected action error when the book is not found" in {
      val authorId = TestAuthors.mehmetAkifTutuncu.id
      val bookId   = UUID.randomUUID
      val payload  = UpdateBookView("", "", 1)
      val error    = Errors.unexpectedAction("update", 1, "book", 0)

      val body = {
        val maybe = putRequest(bookController.update, s"/authors/$authorId/books/$bookId", payload.asJson, Status.fromCode(error.code))
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe error.asJson
    }

    "return unexpected action error when the book doesn't belong to the author" in {
      val authorId = TestAuthors.mehmetAkifTutuncu.id
      val bookId   = TestBooks.icimizdekiSeytan.id
      val payload  = UpdateBookView("", "", 1)
      val error    = Errors.unexpectedAction("update", 1, "book", 0)

      val body = {
        val maybe = putRequest(bookController.update, s"/authors/$authorId/books/$bookId", payload.asJson, Status.fromCode(error.code))
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe error.asJson
    }

    "update the book and return updated book" in {
      val author  = TestAuthors.mehmetAkifTutuncu
      val book    = TestBooks.test1
      val payload = UpdateBookView("Foo", book.isbn, 100)

      val body = {
        val maybe = putRequest(bookController.update, s"/authors/${author.id}/books/${book.id}", payload.asJson)
        maybe.isDefined shouldBe true
        maybe.get
      }

      val bookView = {
        val maybe = body.as[BookView]
        maybe.isRight shouldBe true
        maybe.right.get
      }

      bookView.name   shouldBe payload.name
      bookView.isbn   shouldBe payload.isbn
      bookView.author shouldBe author.name
      bookView.price  shouldBe payload.price

      val updatedBook = {
        val maybe = bookDAO.getByAuthorIdAndBookId(author.id, bookView.id)
        maybe should haveValue
        maybe.right.get
      }

      updatedBook.isbn     shouldBe bookView.isbn
      updatedBook.name     shouldBe bookView.name
      updatedBook.authorId shouldBe author.id
      updatedBook.price    shouldBe bookView.price
    }
  }

  "Deleting a book" should {
    "return unexpected action error when the author is not found" in {
      val authorId = UUID.randomUUID
      val bookId   = TestBooks.icimizdekiSeytan.id
      val error    = Errors.unexpectedAction("delete", 1, "book", 0)

      val body = {
        val maybe = deleteRequest(bookController.delete, s"/authors/$authorId/books/$bookId", Status.fromCode(error.code))
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe error.asJson
    }

    "return unexpected action error when the book is not found" in {
      val authorId = TestAuthors.mehmetAkifTutuncu.id
      val bookId   = UUID.randomUUID
      val error    = Errors.unexpectedAction("delete", 1, "book", 0)

      val body = {
        val maybe = deleteRequest(bookController.delete, s"/authors/$authorId/books/$bookId", Status.fromCode(error.code))
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe error.asJson
    }

    "return unexpected action error when the book doesn't belong to the author" in {
      val authorId = TestAuthors.mehmetAkifTutuncu.id
      val bookId   = TestBooks.icimizdekiSeytan.id
      val error    = Errors.unexpectedAction("delete", 1, "book", 0)

      val body = {
        val maybe = deleteRequest(bookController.delete, s"/authors/$authorId/books/$bookId", Status.fromCode(error.code))
        maybe.isDefined shouldBe true
        maybe.get
      }

      body shouldBe error.asJson
    }

    "delete the book and return nothing" in {
      val author  = TestAuthors.mehmetAkifTutuncu
      val book    = TestBooks.test1

      deleteRequest(bookController.delete, s"/authors/${author.id}/books/${book.id}", needBody = false)

      val expected = Errors.notFound("Book", "authorId" -> author.id.toString, "bookId" -> book.id.toString)

      bookDAO.getByAuthorIdAndBookId(author.id, book.id) should haveError[Book](expected)
    }
  }
}

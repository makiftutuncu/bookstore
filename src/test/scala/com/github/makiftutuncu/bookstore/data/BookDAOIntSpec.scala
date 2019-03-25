package com.github.makiftutuncu.bookstore.data

import java.util.UUID

import cats.Id
import com.github.makiftutuncu.bookstore.models.Book
import com.github.makiftutuncu.bookstore.utilities.Errors
import com.github.makiftutuncu.bookstore.views.{CreateBookView, UpdateBookView}
import com.github.makiftutuncu.bookstore.{IntSpec, TestData}
import org.scalatest.BeforeAndAfterEach

class BookDAOIntSpec extends IntSpec with TestData with BeforeAndAfterEach {
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

  lazy val mockBookDAO: BookDAO[Id] = new BookDAO[Id](failingDatabase)

  "Getting all books" should {
    "return error when there is a DB error" in {
      val expected = Errors.database(testException)

      val result = mockBookDAO.getAll

      result should haveError[List[Book]](expected)
    }

    "return empty list when there is no book" in {
      TestBooks.truncate()

      val expected = List.empty[Book]

      val result = bookDAO.getAll

      result should haveValue[List[Book]](expected)
    }

    "return all books in ascending order by name and price when some exist" in {
      val expected = TestBooks.all

      val result = bookDAO.getAll

      result should haveValue[List[Book]](expected)
    }
  }

  "Getting a book by author id and book id" should {
    "return error when there is a DB error" in {
      val expected = Errors.database(testException)

      val result = mockBookDAO.getByAuthorIdAndBookId(UUID.randomUUID, UUID.randomUUID)

      result should haveError[Book](expected)
    }

    "return not found error when there is no author with given id" in {
      val authorId = UUID.randomUUID
      val bookId   = TestBooks.icimizdekiSeytan.id

      val expected = Errors.notFound("Book", "authorId" -> authorId.toString, "bookId" -> bookId.toString)

      val result = bookDAO.getByAuthorIdAndBookId(authorId, bookId)

      result should haveError[Book](expected)
    }

    "return not found error when there is no book with given id" in {
      val authorId = TestAuthors.mehmetAkifTutuncu.id
      val bookId   = UUID.randomUUID

      val expected = Errors.notFound("Book", "authorId" -> authorId.toString, "bookId" -> bookId.toString)

      val result = bookDAO.getByAuthorIdAndBookId(authorId, bookId)

      result should haveError[Book](expected)
    }

    "return not found error when book with given id doesn't belong to the author with given id" in {
      val authorId = TestAuthors.mehmetAkifTutuncu.id
      val bookId   = TestBooks.icimizdekiSeytan.id

      val expected = Errors.notFound("Book", "authorId" -> authorId.toString, "bookId" -> bookId.toString)

      val result = bookDAO.getByAuthorIdAndBookId(authorId, bookId)

      result should haveError[Book](expected)
    }

    "return a book by author id and book id successfully" in {
      val authorId = TestAuthors.sabahattinAli.id
      val bookId   = TestBooks.kuyucakliYusuf.id

      val expected = TestBooks.kuyucakliYusuf

      val result = bookDAO.getByAuthorIdAndBookId(authorId, bookId)

      result should haveValue[Book](expected)
    }
  }

  "Getting books by name" should {
    "return error when there is a DB error" in {
      val expected = Errors.database(testException)

      val result = mockBookDAO.getByName("test")

      result should haveError[List[Book]](expected)
    }

    "return empty list when there is no book matching given name" in {
      val expected = List.empty[Book]

      val result = bookDAO.getByName("foo")

      result should haveValue[List[Book]](expected)
    }

    "return books matching given name in ascending order by name and price when some exist" in {
      val name = TestBooks.test1.name

      val expected = List(TestBooks.test1, TestBooks.test2)

      val result = bookDAO.getByName(name)

      result should haveValue[List[Book]](expected)
    }
  }

  "Creating a book" should {
    "return error when there is a DB error" in {
      val createView = CreateBookView("ISBN", "Test", 100)

      val expected = Errors.database(testException)

      val result = mockBookDAO.create(UUID.randomUUID, createView)

      result should haveError[Book](expected)
    }

    "return required error when author with given id doesn't exist" in {
      val authorId   = UUID.randomUUID
      val createView = CreateBookView("ISBN5", "Test", 100)

      val expected = Errors.required("author_id", authorId.toString, "authors")

      val result = bookDAO.create(authorId, createView)

      result should haveError[Book](expected)
    }

    "return already exists error when book with given isbn already exists" in {
      val authorId   = TestAuthors.mehmetAkifTutuncu.id
      val createView = CreateBookView(TestBooks.test1.isbn, "Test", 100)

      val expected = Errors.alreadyExists("Book", "isbn", TestBooks.test1.isbn)

      val result = bookDAO.create(authorId, createView)

      result should haveError[Book](expected)
    }

    "create a new book successfully" in {
      val authorId   = TestAuthors.mehmetAkifTutuncu.id
      val createView = CreateBookView("ISBN5", "Test", 100)

      val createdBook = {
        val maybeCreatedBook = bookDAO.create(authorId, createView)
        maybeCreatedBook should haveValue
        maybeCreatedBook.get
      }

      createdBook.authorId shouldBe authorId
      createdBook.name     shouldBe createView.name
      createdBook.isbn     shouldBe createView.isbn
      createdBook.price    shouldBe createView.price

      bookDAO.getByAuthorIdAndBookId(authorId, createdBook.id) should haveValue[Book](createdBook)
    }
  }

  "Updating a book" should {
    "return error when there is a DB error" in {
      val authorId   = UUID.randomUUID
      val bookId     = UUID.randomUUID
      val updateView = UpdateBookView("", "", 1)

      val expected = Errors.database(testException)

      val result = mockBookDAO.update(authorId, bookId, updateView)

      result should haveError[Book](expected)
    }

    "return unexpected action error when there is no book with given author id and book id" in {
      val authorId   = UUID.randomUUID
      val bookId     = UUID.randomUUID
      val updateView = UpdateBookView("", "", 1)

      val expected = Errors.unexpectedAction("update", 1, "book", 0)

      val result = bookDAO.update(authorId, bookId, updateView)

      result should haveError[Book](expected)
    }

    "update a book successfully" in {
      val authorId   = TestAuthors.sabahattinAli.id
      val book       = TestBooks.icimizdekiSeytan
      val updateView = UpdateBookView("Test", book.isbn, 100)

      val updatedBook = {
        val maybeUpdatedBook = bookDAO.update(authorId, book.id, updateView)
        maybeUpdatedBook should haveValue
        maybeUpdatedBook.get
      }

      updatedBook.name  shouldBe updateView.name
      updatedBook.price shouldBe updateView.price

      bookDAO.getByAuthorIdAndBookId(authorId, book.id) should haveValue[Book](updatedBook)
    }
  }

  "Deleting a book" should {
    "return error when there is a DB error" in {
      val authorId = UUID.randomUUID
      val bookId   = UUID.randomUUID

      val expected = Errors.database(testException)

      val result = mockBookDAO.delete(authorId, bookId)

      result should haveError[Unit](expected)
    }

    "return unexpected action error when there is no book with given author id and book id" in {
      val authorId = UUID.randomUUID
      val bookId   = UUID.randomUUID

      val expected = Errors.unexpectedAction("delete", 1, "book", 0)

      val result = bookDAO.delete(authorId, bookId)

      result should haveError[Unit](expected)
    }

    "delete a book successfully" in {
      val book = TestBooks.test2

      val result = bookDAO.delete(book.authorId, book.id)

      result should haveValue

      val expected = Errors.notFound("Book", "authorId" -> book.authorId.toString, "bookId" -> book.id.toString)

      bookDAO.getByAuthorIdAndBookId(book.authorId, book.id) should haveError[Book](expected)
    }
  }
}

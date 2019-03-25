package com.github.makiftutuncu.bookstore.services

import java.util.UUID

import cats.Id
import com.github.makiftutuncu.bookstore.data.BookDAO
import com.github.makiftutuncu.bookstore.utilities.{Errors, Maybe}
import com.github.makiftutuncu.bookstore.views._
import com.github.makiftutuncu.bookstore.{TestData, UnitSpec}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when

class BookServiceUnitSpec extends UnitSpec with TestData {
  lazy val mockAuthorService: AuthorService[Id] = mock[AuthorService[Id]]
  lazy val mockBookDAO: BookDAO[Id]             = mock[BookDAO[Id]]

  val authorView: AuthorView = AuthorView(UUID.randomUUID, "test")

  override lazy val bookService: BookService[Id] = new BookService[Id](mockBookDAO, mockAuthorService)

  "Getting all books" should {
    "return error when there is an error" in {
      val error = Errors.database("test")

      when(mockBookDAO.getAll).thenReturn(Maybe.error(error))

      val result = bookService.getAll

      result should haveError[List[BookView]](error)
    }

    "return all books as book views" in {
      when(mockAuthorService.getById(any[UUID])).thenReturn(Maybe.value(authorView))
      when(mockBookDAO.getAll).thenReturn(Maybe.value(TestBooks.all))

      val expected = TestBooks.all.map(_.toView(authorView.name))

      val result = bookService.getAll

      result should haveValue[List[BookView]](expected)
    }
  }

  "Getting an book by author id and book id" should {
    "return error when there is an error" in {
      val authorId = UUID.randomUUID
      val bookId   = UUID.randomUUID
      val error    = Errors.database("test")

      when(mockBookDAO.getByAuthorIdAndBookId(authorId, bookId)).thenReturn(Maybe.error(error))

      val result = bookService.getByAuthorIdAndBookId(authorId, bookId)

      result should haveError[BookView](error)
    }

    "return a book as book view" in {
      val book = TestBooks.test1

      when(mockBookDAO.getByAuthorIdAndBookId(book.authorId, book.id)).thenReturn(Maybe.value(book))

      val expected = book.toView(authorView.name)

      val result = bookService.getByAuthorIdAndBookId(book.authorId, book.id)

      result should haveValue[BookView](expected)
    }
  }

  "Getting books by name" should {
    "return error when there is an error" in {
      val error = Errors.database("test")

      when(mockBookDAO.getByName("test")).thenReturn(Maybe.error(error))

      val result = bookService.getByName("test")

      result should haveError[List[BookView]](error)
    }

    "return some books as book views" in {
      val book = TestBooks.test1

      when(mockBookDAO.getByName(book.name)).thenReturn(Maybe.value(List(book)))

      val expected = List(book.toView(authorView.name))

      val result = bookService.getByName(book.name)

      result should haveValue[List[BookView]](expected)
    }
  }

  "Creating a book" should {
    "return error when there is an error" in {
      val authorId   = UUID.randomUUID
      val createView = CreateBookView("", "", 1)
      val error      = Errors.database("test")

      when(mockBookDAO.create(authorId, createView)).thenReturn(Maybe.error(error))

      val result = bookService.create(authorId, createView)

      result should haveError[BookView](error)
    }

    "return created book as book view" in {
      val authorId   = UUID.randomUUID
      val createView = CreateBookView("", "", 1)
      val book       = TestBooks.test1

      when(mockBookDAO.create(authorId, createView)).thenReturn(Maybe.value(book))

      val expected = book.toView(authorView.name)

      val result = bookService.create(authorId, createView)

      result should haveValue[BookView](expected)
    }
  }

  "Updating a book" should {
    "return error when there is an error" in {
      val authorId    = UUID.randomUUID
      val bookId      = UUID.randomUUID
      val updateView  = UpdateBookView("", "", 1)
      val error       = Errors.database("test")

      when(mockBookDAO.update(authorId, bookId, updateView)).thenReturn(Maybe.error(error))

      val result = bookService.update(authorId, bookId, updateView)

      result should haveError[BookView](error)
    }

    "return updated book as book view" in {
      val updateView  = UpdateBookView("", "", 1)
      val book        = TestBooks.test1
      val updatedBook = book.copy(name = "test")

      when(mockBookDAO.update(book.authorId, book.id, updateView)).thenReturn(Maybe.value(updatedBook))

      val expected = updatedBook.toView(authorView.name)

      val result = bookService.update(book.authorId, book.id, updateView)

      result should haveValue[BookView](expected)
    }
  }

  "Deleting a book" should {
    "return error when there is an error" in {
      val authorId = UUID.randomUUID
      val bookId   = UUID.randomUUID
      val error    = Errors.database("test")

      when(mockBookDAO.delete(authorId, bookId)).thenReturn(Maybe.error(error))

      val result = bookService.delete(authorId, bookId)

      result should haveError[Unit](error)
    }

    "return nothing when book is deleted" in {
      val authorId = UUID.randomUUID
      val bookId   = UUID.randomUUID

      when(mockBookDAO.delete(authorId, bookId)).thenReturn(Maybe.unit)

      val result = bookService.delete(authorId, bookId)

      result should haveValue
    }
  }
}

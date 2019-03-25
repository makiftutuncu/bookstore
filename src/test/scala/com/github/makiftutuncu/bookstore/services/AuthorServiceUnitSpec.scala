package com.github.makiftutuncu.bookstore.services

import java.util.UUID

import cats.Id
import com.github.makiftutuncu.bookstore.data.AuthorDAO
import com.github.makiftutuncu.bookstore.models.Author
import com.github.makiftutuncu.bookstore.utilities.{Errors, Maybe}
import com.github.makiftutuncu.bookstore.views.{AuthorView, CreateAuthorView, UpdateAuthorView}
import com.github.makiftutuncu.bookstore.{TestData, UnitSpec}
import org.mockito.Mockito.when

class AuthorServiceUnitSpec extends UnitSpec with TestData {
  lazy val mockAuthorDAO: AuthorDAO[Id] = mock[AuthorDAO[Id]]

  override lazy val authorService: AuthorService[Id] = new AuthorService[Id](mockAuthorDAO)

  "Getting all authors" should {
    "return error when there is an error" in {
      val error = Errors.database("test")

      when(mockAuthorDAO.getAll).thenReturn(Maybe.error(error))

      val result = authorService.getAll

      result should haveError[List[AuthorView]](error)
    }

    "return all authors as author views" in {
      when(mockAuthorDAO.getAll).thenReturn(Maybe.value(TestAuthors.all))

      val expected = TestAuthors.all.map(_.toView)

      val result = authorService.getAll

      result should haveValue[List[AuthorView]](expected)
    }
  }

  "Getting an author by id" should {
    "return error when there is an error" in {
      val id    = UUID.randomUUID
      val error = Errors.database("test")

      when(mockAuthorDAO.getById(id)).thenReturn(Maybe.error(error))

      val result = authorService.getById(id)

      result should haveError[AuthorView](error)
    }

    "return the author as author view" in {
      val author = TestAuthors.mehmetAkifTutuncu

      when(mockAuthorDAO.getById(author.id)).thenReturn(Maybe.value(author))

      val expected = author.toView

      val result = authorService.getById(author.id)

      result should haveValue[AuthorView](expected)
    }
  }

  "Getting authors by name" should {
    "return error when there is an error" in {
      val error = Errors.database("test")

      when(mockAuthorDAO.getByName("test")).thenReturn(Maybe.error(error))

      val result = authorService.getByName("test")

      result should haveError[List[AuthorView]](error)
    }

    "return the authors as author views" in {
      val author = TestAuthors.mehmetAkifTutuncu

      when(mockAuthorDAO.getByName(author.name)).thenReturn(Maybe.value(List(author)))

      val expected = List(author.toView)

      val result = authorService.getByName(author.name)

      result should haveValue[List[AuthorView]](expected)
    }
  }

  "Creating an author" should {
    "return error when there is an error" in {
      val createView = CreateAuthorView("")
      val error      = Errors.database("test")

      when(mockAuthorDAO.create(createView)).thenReturn(Maybe.error(error))

      val result = authorService.create(createView)

      result should haveError[AuthorView](error)
    }

    "return created author as author view" in {
      val createView = CreateAuthorView("")
      val author     = Author(UUID.randomUUID, "Test")

      when(mockAuthorDAO.create(createView)).thenReturn(Maybe.value(author))

      val expected = author.toView

      val result = authorService.create(createView)

      result should haveValue[AuthorView](expected)
    }
  }

  "Updating an author" should {
    "return error when updating the author fails" in {
      val id         = UUID.randomUUID
      val updateView = UpdateAuthorView("")
      val error      = Errors.database("test")

      when(mockAuthorDAO.update(id, updateView)).thenReturn(Maybe.error(error))

      val result = authorService.update(id, updateView)

      result should haveError[AuthorView](error)
    }

    "return updated author as author view" in {
      val author        = TestAuthors.mehmetAkifTutuncu
      val updateView    = UpdateAuthorView("")
      val updatedAuthor = author.copy(name = "test")

      when(mockAuthorDAO.update(author.id, updateView)).thenReturn(Maybe.value(updatedAuthor))

      val expected = updatedAuthor.toView

      val result = authorService.update(author.id, updateView)

      result should haveValue[AuthorView](expected)
    }
  }

  "Deleting an author" should {
    "return error when there is an error" in {
      val id    = UUID.randomUUID
      val error = Errors.database("test")

      when(mockAuthorDAO.delete(id)).thenReturn(Maybe.error(error))

      val result = authorService.delete(id)

      result should haveError[Unit](error)
    }

    "return nothing when author is deleted" in {
      val id = UUID.randomUUID

      when(mockAuthorDAO.delete(id)).thenReturn(Maybe.unit)

      val result = authorService.delete(id)

      result should haveValue
    }
  }
}

package com.github.makiftutuncu.bookstore.data

import java.util.UUID

import cats.Id
import com.github.makiftutuncu.bookstore.models.Author
import com.github.makiftutuncu.bookstore.utilities.Errors
import com.github.makiftutuncu.bookstore.views.{CreateAuthorView, UpdateAuthorView}
import com.github.makiftutuncu.bookstore.{IntSpec, TestData}
import org.scalatest.BeforeAndAfterEach

class AuthorDAOIntSpec extends IntSpec with TestData with BeforeAndAfterEach {
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

  lazy val mockAuthorDAO: AuthorDAO[Id] = new AuthorDAO[Id](failingDatabase)

  "Getting all authors" should {
    "return error when there is a DB error" in {
      val expected = Errors.database(testException)

      val result = mockAuthorDAO.getAll

      result should haveError[List[Author]](expected)
    }

    "return empty list when there is no author" in {
      TestAuthors.truncate()

      val expected = List.empty[Author]

      val result = authorDAO.getAll

      result should haveValue[List[Author]](expected)
    }

    "return all authors in alphabetical order when some exist" in {
      val expected = TestAuthors.all

      val result = authorDAO.getAll

      result should haveValue[List[Author]](expected)
    }
  }

  "Getting an author by id" should {
    "return error when there is a DB error" in {
      val expected = Errors.database(testException)

      val result = mockAuthorDAO.getById(UUID.randomUUID)

      result should haveError[Author](expected)
    }

    "return not found error when there is no author with given id" in {
      val id = UUID.randomUUID

      val expected = Errors.notFound("Author", "id" -> id.toString)

      val result = authorDAO.getById(id)

      result should haveError[Author](expected)
    }

    "return an author by id successfully" in {
      val id = TestAuthors.sabahattinAli.id

      val expected = TestAuthors.sabahattinAli

      val result = authorDAO.getById(id)

      result should haveValue[Author](expected)
    }
  }

  "Getting authors by name" should {
    "return error when there is a DB error" in {
      val expected = Errors.database(testException)

      val result = mockAuthorDAO.getByName("test")

      result should haveError[List[Author]](expected)
    }

    "return empty list when there is no author matching given name" in {
      val name = "test"

      val expected = List.empty[Author]

      val result = authorDAO.getByName(name)

      result should haveValue[List[Author]](expected)
    }

    "return authors matching given name in alphabetical order when some exist" in {
      val sabahattinAli2 = Author(UUID.randomUUID, s"${TestAuthors.sabahattinAli.name} 2")
      TestAuthors.insert(sabahattinAli2)

      val name = TestAuthors.sabahattinAli.name

      val expected = List(TestAuthors.sabahattinAli, sabahattinAli2)

      val result = authorDAO.getByName(name)

      result should haveValue[List[Author]](expected)
    }
  }

  "Creating an author" should {
    "return error when there is a DB error" in {
      val author = CreateAuthorView("test")

      val expected = Errors.database(testException)

      val result = mockAuthorDAO.create(author)

      result should haveError[Author](expected)
    }

    "return already exists error when author with given name already exists" in {
      val author = CreateAuthorView(TestAuthors.sabahattinAli.name)

      val expected = Errors.alreadyExists("Author", "name", author.name)

      val result = authorDAO.create(author)

      result should haveError[Author](expected)
    }

    "create a new author successfully" in {
      val author = CreateAuthorView("test")

      val result = authorDAO.create(author)

      result should haveValue

      val createdAuthor = result.get

      createdAuthor.name shouldBe "test"

      authorDAO.getById(createdAuthor.id) should haveValue[Author](createdAuthor)
    }
  }

  "Updating an author" should {
    "return error when there is a DB error" in {
      val expected = Errors.database(testException)

      val result = mockAuthorDAO.update(UUID.randomUUID, UpdateAuthorView("test"))

      result should haveError[Author](expected)
    }

    "return unexpected action error when there is no author with given id" in {
      val updateView = UpdateAuthorView("test")

      val expected = Errors.unexpectedAction("update", 1, "author", 0)

      val result = authorDAO.update(UUID.randomUUID, updateView)

      result should haveError[Author](expected)
    }

    "return already exists error when author with given name already exists" in {
      val updateView = UpdateAuthorView(TestAuthors.mehmetAkifTutuncu.name)

      val expected = Errors.alreadyExists("Author", "name", updateView.name)

      val result = authorDAO.update(TestAuthors.sabahattinAli.id, updateView)

      result should haveError[Author](expected)
    }

    "update an author successfully" in {
      val id         = TestAuthors.sabahattinAli.id
      val updateView = UpdateAuthorView("test")

      val result = authorDAO.update(id, updateView)

      result should haveValue

      val updatedAuthor = result.get

      updatedAuthor.name shouldBe "test"

      authorDAO.getById(id) should haveValue[Author](updatedAuthor)
    }
  }

  "Deleting an author" should {
    "return error when there is a DB error" in {
      val expected = Errors.database(testException)

      val result = mockAuthorDAO.delete(UUID.randomUUID)

      result should haveError[Unit](expected)
    }

    "return unexpected action error when there is no author with given id" in {
      val expected = Errors.unexpectedAction("delete", 1, "author", 0)

      val result = authorDAO.delete(UUID.randomUUID)

      result should haveError[Unit](expected)
    }

    "return still in use error when author still has books" in {
      val id = TestAuthors.mehmetAkifTutuncu.id

      val expected = Errors.inUse("Author", "id", id.toString, "books")

      val result = authorDAO.delete(id)

      result should haveError[Unit](expected)
    }

    "delete an author successfully" in {
      TestBooks.delete(TestBooks.kuyucakliYusuf)
      TestBooks.delete(TestBooks.icimizdekiSeytan)

      val id = TestAuthors.sabahattinAli.id

      val result = authorDAO.delete(id)

      result should haveValue

      val expected = Errors.notFound("Author", "id" -> id.toString)

      authorDAO.getById(id) should haveError[Author](expected)
    }
  }
}

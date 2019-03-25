package com.github.makiftutuncu.bookstore.data

import java.util.UUID

import cats.effect.Effect
import com.github.makiftutuncu.bookstore.models.Book
import com.github.makiftutuncu.bookstore.utilities.{Errors, Maybe}
import com.github.makiftutuncu.bookstore.views.{CreateBookView, UpdateBookView}
import doobie.implicits._
import doobie.util.fragment.Fragment

import scala.language.higherKinds

class BookDAO[F[_]](override val db: Database[F])(implicit F: Effect[F]) extends DAO[F, Book, CreateBookView, UpdateBookView](db) {
  def getAll: Maybe[F, List[Book]] = {
    logger.debug("Getting all books")

    Maybe.attemptF {
      sql"SELECT * FROM books ORDER BY name ASC, price ASC"
        .query[Book]
        .to[List]
        .transact[F](db.transactor)
    } { t =>
      logger.error(s"Failed to get all books!", t)
      Errors.database(t)
    }
  }

  def getByAuthorIdAndBookId(authorId: UUID, bookId: UUID): Maybe[F, Book] = {
    logger.debug(s"Getting book '$bookId' of author '$authorId'")

    Maybe.flatMap {
      Maybe.attemptF {
        sql"SELECT * FROM books WHERE id = $bookId::uuid AND author_id = $authorId::uuid"
          .query[Book]
          .option
          .transact[F](db.transactor)
      } { t =>
        logger.error(s"Failed to get book '$bookId' of author '$authorId'!", t)
        Errors.database(t)
      }
    } {
      case None       => Maybe.error(Errors.notFound("Book", "authorId" -> authorId.toString, "bookId" -> bookId.toString))
      case Some(book) => Maybe.value(book)
    }
  }

  def getByName(name: String): Maybe[F, List[Book]] = {
    logger.debug(s"Getting books by name '$name'")

    Maybe.attemptF {
      sql"SELECT * FROM books WHERE name LIKE ${s"%$name%"} ORDER BY name ASC"
        .query[Book]
        .to[List]
        .transact[F](db.transactor)
    } { t =>
      logger.error(s"Failed to get books by name '$name'!", t)
      Errors.database(t)
    }
  }

  def create(authorId: UUID, createView: CreateBookView): Maybe[F, Book] = {
    logger.debug(s"Creating a new book '$createView' for author '$authorId' as '$createView'")

    lazy val errorTitle = s"Failed to create a new book with '$createView' for author '$authorId' as '$createView'!"

    val book = Book(
      id       = UUID.randomUUID,
      name     = createView.name,
      isbn     = createView.isbn,
      authorId = authorId,
      price    = createView.price
    )

    Maybe.attemptF {
      F.map(
        insertSql(book)
          .update
          .run
          .transact[F](db.transactor)
      ) { _ =>
        book
      }
    } {
      case ForeignKeyInsertViolation(column, value, table) =>
        val error = Errors.required(column, value, table)
        logger.error(s"$errorTitle ${error.message}.")
        error

      case UniqueKeyInsertViolation(column, value) =>
        val error = Errors.alreadyExists("Book", column, value)
        logger.error(s"$errorTitle ${error.message}.")
        error

      case t: Throwable =>
        logger.error(errorTitle, t)
        Errors.database(t)
    }
  }

  def update(authorId: UUID, bookId: UUID, updateView: UpdateBookView): Maybe[F, Book] = {
    logger.debug(s"Updating book '$bookId' of author '$authorId' as '$updateView'")

    lazy val errorTitle = s"Failed to update book '$bookId' of author '$authorId' as '$updateView'!"

    Maybe.attempt {
      F.flatMap(
        sql"UPDATE books SET name = ${updateView.name}, price = ${updateView.price} WHERE id = $bookId::uuid AND author_id = $authorId::uuid"
          .update
          .run
          .transact[F](db.transactor)
      ) { affectedRows =>
        if (affectedRows != 1) {
          val error = Errors.unexpectedAction("update", 1, "book", affectedRows)
          logger.error(s"$errorTitle ${error.message}.")
          Maybe.error[F, Book](error)
        } else {
          Maybe.value[F, Book](
            Book(
              id       = bookId,
              name     = updateView.name,
              isbn     = updateView.isbn,
              authorId = authorId,
              price    = updateView.price
            )
          )
        }
      }
    } { t =>
      logger.error(errorTitle, t)
      Errors.database(t)
    }
  }

  def delete(authorId: UUID, bookId: UUID): Maybe[F, Unit] = {
    logger.debug(s"Deleting book '$bookId' of author '$authorId'")

    lazy val errorTitle = s"Failed to delete book '$bookId' of author '$authorId'!"

    Maybe.attempt {
      F.flatMap(
        deleteSql(authorId, bookId)
          .update
          .run
          .transact[F](db.transactor)
      ) { affectedRows =>
        if (affectedRows != 1) {
          val error = Errors.unexpectedAction("delete", 1, "book", affectedRows)
          logger.error(s"$errorTitle ${error.message}.")
          Maybe.error[F, Unit](error)
        } else {
          Maybe.unit[F]
        }
      }
    } { t =>
      logger.error(errorTitle, t)
      Errors.database(t)
    }
  }

  def insertSql(book: Book): Fragment =
    sql"INSERT INTO books(id, name, isbn, author_id, price) VALUES(${book.id}::uuid, ${book.name}, ${book.isbn}, ${book.authorId}::uuid, ${book.price})"

  def deleteSql(authorId: UUID, bookId: UUID): Fragment =
    sql"DELETE FROM books WHERE id = $bookId::uuid AND author_id = $authorId::uuid"
}

package com.github.makiftutuncu.bookstore.data

import java.util.UUID

import cats.effect.Effect
import com.github.makiftutuncu.bookstore.models.Author
import com.github.makiftutuncu.bookstore.utilities.{Errors, Maybe}
import com.github.makiftutuncu.bookstore.views.{CreateAuthorView, UpdateAuthorView}
import doobie.implicits._
import doobie.util.fragment.Fragment

import scala.language.higherKinds

class AuthorDAO[F[_]](override val db: Database[F])(implicit F: Effect[F]) extends DAO[F, Author, CreateAuthorView, UpdateAuthorView](db) {
  def getAll: Maybe[F, List[Author]] = {
    logger.debug("Getting all authors")

    Maybe.attemptF {
      sql"SELECT * FROM authors ORDER BY name ASC"
        .query[Author]
        .to[List]
        .transact[F](db.transactor)
    } { t =>
      logger.error(s"Failed to get all authors!", t)
      Errors.database(t)
    }
  }

  def getById(id: UUID): Maybe[F, Author] = {
    logger.debug(s"Getting author '$id'")

    Maybe.flatMap {
      Maybe.attemptF {
        sql"SELECT * FROM authors WHERE id = $id::uuid"
          .query[Author]
          .option
          .transact[F](db.transactor)
      } { t =>
        logger.error(s"Failed to get author '$id'!", t)
        Errors.database(t)
      }
    } {
      case None         => Maybe.error(Errors.notFound("Author", "id" -> id.toString))
      case Some(author) => Maybe.value(author)
    }
  }

  def getByName(name: String): Maybe[F, List[Author]] = {
    logger.debug(s"Getting authors by name '$name'")

    Maybe.attemptF {
      sql"SELECT * FROM authors WHERE name LIKE ${s"%$name%"} ORDER BY name ASC"
        .query[Author]
        .to[List]
        .transact[F](db.transactor)
    } { t =>
      logger.error(s"Failed to get authors by name '$name'!", t)
      Errors.database(t)
    }
  }

  def create(createView: CreateAuthorView): Maybe[F, Author] = {
    logger.debug(s"Creating a new author as '$createView'")

    lazy val errorTitle = s"Failed to create a new author as '$createView'!"

    val author = Author(
      id   = UUID.randomUUID,
      name = createView.name
    )

    Maybe.attemptF {
      F.map(
        insertSql(author)
          .update
          .run
          .transact[F](db.transactor)
      ) { _ =>
        author
      }
    } {
      case UniqueKeyInsertViolation(column, value) =>
        val error = Errors.alreadyExists("Author", column, value)
        logger.error(s"$errorTitle ${error.message}.")
        error

      case t: Throwable =>
        logger.error(errorTitle, t)
        Errors.database(t)
    }
  }

  def update(id: UUID, updateView: UpdateAuthorView): Maybe[F, Author] = {
    logger.debug(s"Updating author '$id' as '$updateView'")

    lazy val errorTitle = s"Failed to update author '$id' as '$updateView'!"

    Maybe.attempt {
      F.flatMap(
        sql"UPDATE authors SET name = ${updateView.name} WHERE id = $id::uuid"
          .update
          .run
          .transact[F](db.transactor)
      ) { affectedRows =>
        if (affectedRows != 1) {
          val error = Errors.unexpectedAction("update", 1, "author", affectedRows)
          logger.error(s"$errorTitle ${error.message}.")
          Maybe.error[F, Author](error)
        } else {
          Maybe.value[F, Author](
            Author(
              id   = id,
              name = updateView.name
            )
          )
        }
      }
    } {
      case UniqueKeyInsertViolation(column, value) =>
        val error = Errors.alreadyExists("Author", column, value)
        logger.error(s"$errorTitle ${error.message}.")
        error

      case t: Throwable =>
        logger.error(errorTitle, t)
        Errors.database(t)
    }
  }

  def delete(id: UUID): Maybe[F, Unit] = {
    logger.debug(s"Deleting author '$id'")

    lazy val errorTitle = s"Failed to delete author '$id'!"

    Maybe.attempt {
      F.flatMap(
        deleteSql(id)
          .update
          .run
          .transact[F](db.transactor)
      ) { affectedRows =>
        if (affectedRows != 1) {
          val error = Errors.unexpectedAction("delete", 1, "author", affectedRows)
          logger.error(s"$errorTitle ${error.message}.")
          Maybe.error[F, Unit](error)
        } else {
          Maybe.unit[F]
        }
      }
    } {
      case ForeignKeyDeleteViolation(column, value, table) =>
        val error = Errors.inUse("Author", column, value, table)
        logger.error(s"$errorTitle ${error.message}.")
        error

      case t: Throwable =>
        logger.error(errorTitle, t)
        Errors.database(t)
    }
  }

  def insertSql(author: Author): Fragment =
    sql"INSERT INTO authors(id, name) VALUES(${author.id}::uuid, ${author.name})"

  def deleteSql(id: UUID): Fragment =
    sql"DELETE FROM authors WHERE id = $id::uuid"
}

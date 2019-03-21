package com.github.makiftutuncu.bookstore

import cats.effect.{ContextShift, Effect}
import com.github.makiftutuncu.bookstore.controllers.{AuthorController, BookController}
import com.github.makiftutuncu.bookstore.data.{AuthorDAO, BookDAO, Database}
import com.github.makiftutuncu.bookstore.services.{AuthorService, BookService}
import com.github.makiftutuncu.bookstore.utilities.Config
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

import scala.language.higherKinds

trait Components[F[_]] {
  implicit val contextShift: ContextShift[F]
  implicit val effect: Effect[F]

  lazy val config: Config            = Config.load("com.github.makiftutuncu.bookstore")
  lazy val flyway: Flyway            = Flyway.configure().locations("migrations").dataSource(config.db.jdbc, config.db.user, config.db.pass).load()
  lazy val transactor: Transactor[F] = Transactor.fromDriverManager[F]("org.postgresql.Driver", config.db.jdbc, config.db.user, config.db.pass)

  lazy val database: Database[F] = new Database[F](transactor)

  lazy val authorDAO: AuthorDAO[F] = new AuthorDAO[F](database)
  lazy val bookDAO: BookDAO[F]     = new BookDAO[F](database)

  lazy val authorService: AuthorService[F] = new AuthorService[F](authorDAO)
  lazy val bookService: BookService[F]     = new BookService[F](bookDAO, authorService)

  lazy val authorController: AuthorController[F] = new AuthorController[F](authorService)
  lazy val bookController: BookController[F]     = new BookController[F](bookService)
}

package com.github.makiftutuncu.bookstore

import java.util.UUID

import cats.Id
import com.github.makiftutuncu.bookstore.models.{Author, Book}
import com.github.makiftutuncu.bookstore.views.{AuthorView, BookView}
import doobie.util.fragment.Fragment

trait TestData { self: Components[Id] =>
  lazy val authorsToBooks: Map[Author, List[Book]] =
    Map(
      TestAuthors.sabahattinAli     -> List(TestBooks.icimizdekiSeytan, TestBooks.kuyucakliYusuf),
      TestAuthors.mehmetAkifTutuncu -> List(TestBooks.test1, TestBooks.test2)
    )

  lazy val booksToAuthors: Map[Book, Author] =
    Map(
      TestBooks.icimizdekiSeytan -> TestAuthors.sabahattinAli,
      TestBooks.kuyucakliYusuf   -> TestAuthors.sabahattinAli,
      TestBooks.test1            -> TestAuthors.mehmetAkifTutuncu,
      TestBooks.test2            -> TestAuthors.mehmetAkifTutuncu
    )

  object TestAuthors extends TestDataGenerator[Author]("authors", authorDAO.insertSql, database.transactor) {
    lazy val mehmetAkifTutuncu: Author =
      Author(
        id   = UUID.randomUUID,
        name = "Mehmet Akif Tutuncu"
      )

    lazy val sabahattinAli: Author =
      Author(
        id   = UUID.randomUUID,
        name = "Sabahattin Ali"
      )

    override lazy val all: List[Author] = List(mehmetAkifTutuncu, sabahattinAli)

    lazy val allAuthorViews: List[AuthorView] = all.map(_.toView)

    override protected def deleteSql(author: Author): Fragment = authorDAO.deleteSql(author.id)
  }

  object TestBooks extends TestDataGenerator[Book]("books", bookDAO.insertSql, database.transactor) {
    lazy val icimizdekiSeytan: Book =
      Book(
        id       = UUID.randomUUID,
        name     = "Icimizdeki Seytan",
        isbn     = "ISBN1",
        authorId = TestAuthors.sabahattinAli.id,
        price    = 15000
      )

    lazy val kuyucakliYusuf: Book =
      Book(
        id       = UUID.randomUUID,
        name     = "Kuyucakli Yusuf",
        isbn     = "ISBN2",
        authorId = TestAuthors.sabahattinAli.id,
        price    = 20000
      )

    lazy val test1: Book =
      Book(
        id       = UUID.randomUUID,
        name     = "Test",
        isbn     = "ISBN3",
        authorId = TestAuthors.mehmetAkifTutuncu.id,
        price    = 9000
      )

    lazy val test2: Book =
      Book(
        id       = UUID.randomUUID,
        name     = "Test",
        isbn     = "ISBN4",
        authorId = TestAuthors.mehmetAkifTutuncu.id,
        price    = 10000
      )

    override lazy val all: List[Book] = List(icimizdekiSeytan, kuyucakliYusuf, test1, test2)

    lazy val allBookViews: List[BookView] = booksToAuthors.map(ba => ba._1.toView(ba._2.name)).toList

    override protected def deleteSql(book: Book): Fragment = bookDAO.deleteSql(book.authorId, book.id)
  }
}

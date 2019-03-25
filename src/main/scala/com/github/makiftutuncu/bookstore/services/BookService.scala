package com.github.makiftutuncu.bookstore.services

import java.util.UUID

import cats.Id
import cats.effect.Effect
import com.github.makiftutuncu.bookstore.data.BookDAO
import com.github.makiftutuncu.bookstore.models.Book
import com.github.makiftutuncu.bookstore.utilities.{Convert, Maybe}
import com.github.makiftutuncu.bookstore.views.{BookView, CreateBookView, UpdateBookView}

import scala.language.higherKinds

class BookService[F[_]](override val dao: BookDAO[F], authorService: AuthorService[F])(implicit F: Effect[F]) extends Service[F, Book, BookView, CreateBookView, UpdateBookView](dao) {
  def getAll: Maybe[F, List[BookView]] =
    Maybe.flatMap(dao.getAll) { books =>
      Convert[F, List].convert[Book, BookView](books, toView)
    }

  def getByAuthorIdAndBookId(authorId: UUID, bookId: UUID): Maybe[F, BookView] =
    Maybe.flatMap(dao.getByAuthorIdAndBookId(authorId, bookId)) { book =>
      Convert[F, Id].convert[Book, BookView](book, toView)
    }

  def getByName(name: String): Maybe[F, List[BookView]] =
    Maybe.flatMap(dao.getByName(name)) { books =>
      Convert[F, List].convert[Book, BookView](books, toView)
    }

  def create(authorId: UUID, createView: CreateBookView): Maybe[F, BookView] =
    Maybe.flatMap(dao.create(authorId, createView)) {
      toView
    }

  def update(authorId: UUID, bookId: UUID, updateView: UpdateBookView): Maybe[F, BookView] =
    Maybe.flatMap(dao.update(authorId, bookId, updateView)) {
      toView
    }

  def delete(authorId: UUID, bookId: UUID): Maybe[F, Unit] =
    dao.delete(authorId, bookId)

  def toView(book: Book): Maybe[F, BookView] =
    Maybe.map(authorService.getById(book.authorId)) { author =>
      BookView(
        id     = book.id,
        isbn   = book.isbn,
        name   = book.name,
        author = author.name,
        price  = book.price
      )
    }
}

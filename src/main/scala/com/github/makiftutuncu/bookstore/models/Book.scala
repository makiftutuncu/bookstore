package com.github.makiftutuncu.bookstore.models
import java.util.UUID

import com.github.makiftutuncu.bookstore.views.BookView

final case class Book(override val id: UUID,
                      override val name: String,
                      isbn: String,
                      authorId: UUID,
                      price: Int) extends Model {
  def toView(authorName: String): BookView =
    BookView(
      id     = id,
      isbn   = isbn,
      name   = name,
      author = authorName,
      price  = price
    )
}


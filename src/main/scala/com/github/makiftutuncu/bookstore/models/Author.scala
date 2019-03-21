package com.github.makiftutuncu.bookstore.models

import java.util.UUID

import com.github.makiftutuncu.bookstore.views.AuthorView

final case class Author(override val id: UUID, name: String) extends Model {
  def toView: AuthorView =
    AuthorView(
      id   = id,
      name = name
    )
}

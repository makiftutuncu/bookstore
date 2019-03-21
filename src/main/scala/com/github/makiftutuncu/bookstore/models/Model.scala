package com.github.makiftutuncu.bookstore.models

import java.util.UUID

trait Model {
  val id: UUID
  val name: String
}

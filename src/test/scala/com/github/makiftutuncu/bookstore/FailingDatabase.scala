package com.github.makiftutuncu.bookstore

import cats.Id
import com.github.makiftutuncu.bookstore.data.Database
import com.github.makiftutuncu.bookstore.utilities.BookstoreException
import doobie.util.transactor.Transactor

class FailingDatabase(e: BookstoreException) extends Database[Id](null) {
  override def transactor: Transactor[Id] = throw e
}

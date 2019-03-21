package com.github.makiftutuncu.bookstore.data

import doobie.util.transactor.Transactor

class Database[F[_]](t: Transactor[F]) {
  def transactor: Transactor[F] = t
}

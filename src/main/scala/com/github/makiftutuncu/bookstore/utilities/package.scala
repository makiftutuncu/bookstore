package com.github.makiftutuncu.bookstore

import scala.language.higherKinds

package object utilities {
  type Maybe[F[_], A] = F[Either[BookstoreException, A]]
}

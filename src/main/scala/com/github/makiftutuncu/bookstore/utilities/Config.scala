package com.github.makiftutuncu.bookstore.utilities

import pureconfig.generic.auto._

final case class Config(db: Config.DB, server: Config.Server)

object Config {
  def load(root: String): Config = pureconfig.loadConfigOrThrow[Config](root)

  final case class DB(host: String,
                      port: Int,
                      name: String,
                      user: String,
                      pass: String) {
    val jdbc: String = s"jdbc:postgresql://$host:$port/$name"
  }

  final case class Server(port: Int)
}


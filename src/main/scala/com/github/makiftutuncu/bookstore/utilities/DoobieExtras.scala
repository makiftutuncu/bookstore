package com.github.makiftutuncu.bookstore.utilities

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import doobie.util.Meta
import doobie.util.log.{ExecFailure, LogHandler, ProcessingFailure, Success}
import org.postgresql.util.PSQLException

trait DoobieExtras extends LazyLogging {
  implicit val logHandler: LogHandler = LogHandler {
    case Success(s, a, e1, e2) =>
      logger.trace(
        s"""Successful Statement Execution:
            |
            |  ${s.lines.dropWhile(_.trim.isEmpty).mkString("\n  ")}
            |
            | arguments = [${a.mkString(", ")}]
            |   elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (${(e1 + e2).toMillis} ms total)
          """.stripMargin
      )

    case ProcessingFailure(s, a, e1, e2, t) =>
      logger.error(
        s"""Failed Resultset Processing:
            |
            |  ${s.lines.dropWhile(_.trim.isEmpty).mkString("\n  ")}
            |
            | arguments = [${a.mkString(", ")}]
            |   elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (failed) (${(e1 + e2).toMillis} ms total)
            |   failure = ${t.getMessage}
          """.stripMargin
      )

    case ExecFailure(s, a, e1, t) =>
      logger.error(
        s"""Failed Statement Execution:
            |
            |  ${s.lines.dropWhile(_.trim.isEmpty).mkString("\n  ")}
            |
            | arguments = [${a.mkString(", ")}]
            |   elapsed = ${e1.toMillis} ms exec (failed)
            |   failure = ${t.getMessage}
          """.stripMargin
      )
  }

  implicit val uuidMeta: Meta[UUID] = Meta[String].timap[UUID](UUID.fromString)(_.toString)

  object UniqueKeyInsertViolation {
    private val regex = "Key \\((.+)\\)=\\((.+)\\) already exists".r

    def unapply(e: PSQLException): Option[(String, String)] =
      regex.findFirstMatchIn(e.getMessage).flatMap { m =>
        val matches = m.subgroups

        for {
          key   <- matches.headOption
          value <- matches.lastOption
        } yield {
          key -> value
        }
      }
  }

  object ForeignKeyInsertViolation {
    private val regex = "Key \\((.+)\\)=\\((.+)\\) is not present in table \"(.+)\"".r

    def unapply(e: PSQLException): Option[(String, String, String)] =
      regex.findFirstMatchIn(e.getMessage).flatMap { m =>
        val matches = m.subgroups

        for {
          key   <- matches.headOption
          value <- matches.drop(1).headOption
          table <- matches.lastOption
        } yield {
          (key, value, table)
        }
      }
  }

  object ForeignKeyDeleteViolation {
    private val regex = "Key \\((.+)\\)=\\((.+)\\) is still referenced from table \"(.+)\"".r

    def unapply(e: PSQLException): Option[(String, String, String)] =
      regex.findFirstMatchIn(e.getMessage).flatMap { m =>
        val matches = m.subgroups

        for {
          key   <- matches.headOption
          value <- matches.drop(1).headOption
          table <- matches.lastOption
        } yield {
          (key, value, table)
        }
      }
  }
}

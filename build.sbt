organization := "com.github.makiftutuncu"
name         := "bookstore"
version      := "0.1"
scalaVersion := "2.12.8"

fork in Test := true
javaOptions in Test += "-Dconfig.resource=test.conf"

libraryDependencies ++= Seq(
  // Cats
  "org.typelevel" %% "cats-core"   % "1.6.0",
  "org.typelevel" %% "cats-effect" % "1.2.0",

  // Circe
  "io.circe" %% "circe-core"    % "0.10.0",
  "io.circe" %% "circe-generic" % "0.10.0",

  // Config
  "com.typesafe"           % "config"     % "1.3.3",
  "com.github.pureconfig" %% "pureconfig" % "0.10.0",

  // Doobie
  "org.tpolecat" %% "doobie-core"     % "0.6.0",
  "org.tpolecat" %% "doobie-postgres" % "0.6.0",
  
  // Finch
  "com.github.finagle" %% "finchx-core"  % "0.27.0",
  "com.github.finagle" %% "finchx-circe" % "0.27.0",
  "com.github.finagle" %% "finchx-test"  % "0.27.0",
  
  // Flyway
  "org.flywaydb" % "flyway-core" % "5.2.4",

  // Logging
  "ch.qos.logback"              % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.0",

  // Mockito
  "org.mockito" % "mockito-core" % "2.25.1" % Test,
  
  // PostgreSQL
  "org.postgresql" % "postgresql" % "42.2.5",

  // ScalaTest
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

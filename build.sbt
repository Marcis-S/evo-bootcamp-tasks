name := "evo_bootcamp_tasks"

version := "0.1"

scalaVersion := "2.13.3"

val circeVersion = "0.13.0"
val catsVersion = "2.2.0"
val http4sVersion = "0.21.7"

scalacOptions in Global += "-Ymacro-annotations"

libraryDependencies ++= Seq(

  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,
  "io.circe" %% "circe-optics" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.scalactic" %% "scalactic" % "3.2.0",
  "org.typelevel" %% "cats-core" % "2.0.0",
  "org.scalaj" %% "scalaj-http" % "2.4.2" % Test,
  "org.scalatest" %% "scalatest" % "3.2.0" % Test,
"org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2" % Test

)
// https://mvnrepository.com/artifact/com.github.blemale/scaffeine
libraryDependencies += "com.github.blemale" %% "scaffeine" % "4.0.2"

libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0"
libraryDependencies += "com.codecommit" %% "cats-effect-testing-scalatest" % "0.4.1" % Test

name := "evo_bootcamp_tasks"

version := "0.1"

scalaVersion := "2.13.3"

val circeVersion = "0.13.0"
val catsVersion = "2.2.0"

scalacOptions in Global += "-Ymacro-annotations"

libraryDependencies ++= Seq(
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
libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0"
libraryDependencies += "com.codecommit" %% "cats-effect-testing-scalatest" % "0.4.1" % Test

name := "evo_bootcamp_tasks"

version := "0.1"

scalaVersion := "2.13.3"
//scalacOptions += "-Ypartial-unification"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.0"
//libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test
libraryDependencies += "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2" % Test
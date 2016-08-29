name := "scetris"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-swing" % "1.0.2",
  "com.typesafe.akka" %% "akka-stream" % "2.4.9",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)
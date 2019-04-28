name := "scetris"

version := "1.0"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-swing" % "2.1.1",
//  "com.typesafe.akka" %% "akka-stream" % "2.5.9",
  "co.fs2"        %% "fs2-core"  % "1.0.4",
  "org.scalatest" %% "scalatest" % "3.0.7" % "test"
)

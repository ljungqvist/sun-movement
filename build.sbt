
name := """sun-movement"""
organization := "info.ljungqvist"
version := "0.1-SNAPSHOT"

licenses += ("Apache 2", url("http://www.apache.org/licenses/LICENSE-2.0"))

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.7",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",

    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

bintraySettings

com.typesafe.sbt.SbtGit.versionWithGit

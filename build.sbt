name := """sun-movement"""

organization := "info.ljungqvist"

version := "0.1-SNAPSHOT"

licenses += ("Apache 2", url("http://www.apache.org/licenses/LICENSE-2.0"))

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

bintraySettings

com.typesafe.sbt.SbtGit.versionWithGit

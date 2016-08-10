name := """sun-movement"""

organization := "info.ljungqvist"

licenses += ("Apache 2", url("http://www.apache.org/licenses/LICENSE-2.0"))

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

bintraySettings

com.typesafe.sbt.SbtGit.versionWithGit

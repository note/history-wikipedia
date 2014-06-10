name := """web"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

resolvers += "Gephi Snapshots" at "http://nexus.gephi.org/nexus/content/repositories/snapshots/"

resolvers += "Gephi Releases" at "http://nexus.gephi.org/nexus/content/repositories/releases/"

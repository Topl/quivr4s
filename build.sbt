name := "quivr4s"

version := "0.1"

scalaVersion := "2.13.9"

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.8.0",
  "org.typelevel" %% "cats-effect" % "3.3.14",
  "org.scorexfoundation" %% "scrypto" % "2.2.1"
)

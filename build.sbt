name := "quivr4s"

organization := "co.topl"

version := "0.1"

scalaVersion := "2.13.9"

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"

lazy val catsVersion = "2.8.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-free" % catsVersion,
  "org.typelevel" %% "cats-effect" % "3.3.14",
  "org.scorexfoundation" %% "scrypto" % "2.2.1",
  "co.topl" %% "crypto" % "1.10.2" // https://mvnrepository.com/artifact/co.topl/crypto

)

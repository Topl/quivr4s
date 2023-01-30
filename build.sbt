name := "quivr4s"

organization := "co.topl"

version := "0.1"

scalaVersion := "2.13.9"

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
resolvers += "jitpack" at "https://jitpack.io"

lazy val catsVersion = "2.9.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-free" % catsVersion,
  "org.typelevel" %% "cats-effect" % "3.4.1",
  "org.scorexfoundation" %% "scrypto" % "2.2.1",
  "co.topl" %% "crypto" % "1.10.2",
  "org.typelevel" %% "simulacrum" % "1.0.1",
  "org.scalameta" %% "munit" % "0.7.29" % Test,
  "com.github.Topl" % "protobuf-specs" % "21ac57d"
)

// For Scala 2.13+
scalacOptions += "-Ymacro-annotations"

scalacOptions += "-Ywarn-unused"

semanticdbEnabled := true

semanticdbVersion := scalafixSemanticdb.revision

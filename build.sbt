inThisBuild(
  List(
    organization := "co.topl",
    licenses := Seq("MPL2.0" -> url("https://www.mozilla.org/en-US/MPL/2.0/")),
    scalaVersion := "2.13.11"
  )
)

lazy val commonScalacOptions = Seq(
  "-Ymacro-annotations",
  "-Ywarn-unused"
)

lazy val commonSettings = Seq(
  scalacOptions ++= commonScalacOptions,
  semanticdbEnabled := true, // enable SemanticDB for Scalafix
  semanticdbVersion := scalafixSemanticdb.revision,
  resolvers ++= Seq(
    "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
    "Sonatype Releases s01" at "https://s01.oss.sonatype.org/content/repositories/releases/",
    "jitpack" at "https://jitpack.io"
  )
)

lazy val publishSettings = Seq(
  homepage := Some(url("https://github.com/Topl/quivr4s")),
  ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org",
  sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
  Test / publishArtifact := true,
  pomIncludeRepository := { _ => false },
  pomExtra :=
    <developers>
      <developer>
        <id>scasplte2</id>
        <name>James Aman</name>
      </developer>
    </developers>
)

lazy val quivr4s = project
  .in(file("."))
  .settings(
    moduleName := "quivr4s",
    commonSettings,
    publishSettings,
    libraryDependencies ++=
      Dependencies.sourcesDependencies ++
      Dependencies.testsDependencies
  )

addCommandAlias("checkPR", s"; scalafixAll --check; scalafmtCheckAll; +test")
addCommandAlias("preparePR", s"; scalafixAll; scalafmtAll; +test")
addCommandAlias("checkPRTestQuick", s"; scalafixAll --check; scalafmtCheckAll; testQuick")

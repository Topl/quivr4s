import sbt._

object Dependencies {

  lazy val catsVersion = "2.9.0"

  val sourcesDependencies = Seq(
    "org.typelevel"   %% "cats-core"      % catsVersion,
    "org.typelevel"   %% "cats-free"      % catsVersion,
    "org.typelevel"   %% "cats-effect"    % "3.4.8",
    "org.bouncycastle" % "bcprov-jdk18on" % "1.72",
    "org.typelevel"   %% "simulacrum"     % "1.0.1",
    "co.topl"         %% "protobuf-fs2"   % "2.0.0-alpha1" % "provided"
  )

  val testsDependencies = Seq(
    "org.scalameta"  %% "munit"      % "0.7.29",
    "org.scalacheck" %% "scalacheck" % "1.17.0"
  ).map(_ % Test)

}

import sbt._

object Dependencies {

  lazy val catsVersion = "2.9.0"

  val sourcesDependencies = Seq(
    "org.typelevel"        %% "cats-core"      % catsVersion,
    "org.typelevel"        %% "cats-free"      % catsVersion,
    "org.typelevel"        %% "cats-effect"    % "3.4.8",
    "org.scorexfoundation" %% "scrypto"        % "2.2.1",
    "co.topl"              %% "crypto"         % "1.10.2",
    "org.typelevel"        %% "simulacrum"     % "1.0.1",
    "com.github.Topl"       % "protobuf-specs" % "7d2ba5c" // scala-steward:off
  )

  val testsDependencies = Seq(
    "org.scalameta"  %% "munit"      % "0.7.29",
    "org.scalacheck" %% "scalacheck" % "1.17.0"
  ).map(_ % Test)

}

package co.topl.quivr.archive.v2

sealed abstract class Proposition

object Propositions {
  case class Digest(digest: Array[Byte]) extends Proposition

  object Digest {
    val token: Byte = 1: Byte
  }
}

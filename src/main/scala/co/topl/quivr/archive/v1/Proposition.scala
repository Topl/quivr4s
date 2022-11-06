package co.topl.quivr.v1

sealed abstract class Proposition 

object Propositions {
    case class Digest(digest: Array[Byte]) extends Proposition
}

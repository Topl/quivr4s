package co.topl.quivr.archive.v1

sealed abstract class Proof

object Proofs {
    case class Digest(preimage: Array[Byte]) extends Proof
}

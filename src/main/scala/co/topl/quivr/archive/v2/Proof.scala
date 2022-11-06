package co.topl.quivr.archive.v2

sealed abstract class Proof

object Proofs {
    case class Digest(preimage: Array[Byte], witness: Array[Byte]) extends Proof
}

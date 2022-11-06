package co.topl.quivr.v2


sealed abstract class Proof

object Proofs {
    case class Digest(preimage: Array[Byte], witness: Array[Byte]) extends Proof
}

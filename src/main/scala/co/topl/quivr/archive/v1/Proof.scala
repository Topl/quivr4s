package co.topl.quivr.v1


sealed abstract class Proof

object Proofs {
    case class Digest(preimage: Array[Byte]) extends Proof
}

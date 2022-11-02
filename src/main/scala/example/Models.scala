import co.topl.crypto.hash._

sealed abstract class QProposition
sealed abstract class QProof
case class EvalContext(a: Int, b: Int, c: Int)

object Models {

    object Digest {
        def propose(digest: Array[Byte]): Proposition =  Proposition(digest)
        def prove(preimage: Array[Byte]): Proof = Proof(preimage)
        def verify(proposition: Proposition, proof: Proof): Boolean = blake2b256.hash(proof.preimage).value sameElements proposition.digest

        case class Proposition(digest: Array[Byte]) extends QProposition
        case class Proof(preimage: Array[Byte]) extends QProof
    }
}
package co.topl.brambl

import cats.Monad
import co.topl.quivr.Models.Primitive
import co.topl.quivr.{
  Proof,
  Proposer,
  Proposition,
  Prover,
  SignableTxBytes,
  runtime,
  Verifier
}
import co.topl.quivr.runtime.{DynamicContext}
import co.topl.common.{Digest, DigestVerification, Preimage}
import co.topl.brambl.Models.Signable
import co.topl.node.Tetra.Datums
import co.topl.quivr.algebras.DigestVerifier
import co.topl.crypto.hash.blake2b256

case class DigestError() extends runtime.Error

object QuivrService {
  type Trivial[T] = T

  // An Opinionated Verification Context
  case class ToplContext(tx: Signable) extends DynamicContext[Trivial, String] {
    override val datums = Map(
      // Datums with height
      "eon" -> Datums.Eon(10, 2), // <- not sure what "beginSlot" is referring to. First slot of the eon?
      "era" -> Datums.Era(22, 4),
      "epoch" -> Datums.Epoch(34, 6),
      "header" -> Datums.Header(24, None),
      "body" -> Datums.Body(Array(0: Byte), None)
    )

    override val interfaces = ???
    override val signingRoutines = ???


    private case class DigestValidator() extends DigestVerifier[Trivial] {
      override def validate(v: DigestVerification) = {
        // not sure if this is how salt should be used
        // there's 2 salts; Preimage.salt and DigestVerification.salt
        val test =  blake2b256.hash(v.preimage.input ++ v.preimage.salt).value
        val expected = v.digest.value
        if(expected sameElements test) Right(v)
        else Left(DigestError())
      }
    }
    override val hashingRoutines = Map(
      "blake2b256" -> DigestValidator()
    )

    override def signableBytes: SignableTxBytes = tx.getSignableBytes
    override def currentTick: Long = 250L // Arbitrary value

  }

  val ctx: Signable => ToplContext = ToplContext

  def getDigestProposition(digest: Digest): Trivial[Proposition] =
    Proposer.digestProposer[Trivial, (String, Digest)].propose(("blake2b256", digest))

  def getDigestProof(preImage: Preimage, message: SignableTxBytes): Trivial[Proof] = {
    val prover: Prover[Trivial, (Byte, Preimage)] = Prover.instances.proverInstance
    prover.prove((Primitive.Digest.token, preImage), message)
  }

  def verify(proposition: Proposition, proof: Proof)(tx: Signable): Trivial[Boolean] = {
    val verifier: Verifier[Trivial] = Verifier.instances.verifierInstance
    verifier.evaluate(proposition, proof, ctx(tx))
  }
}

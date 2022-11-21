package co.topl.brambl

import co.topl.quivr.Models.Primitive
import co.topl.quivr.{Proof, Proposer, Proposition, Prover, SignableTxBytes}
import co.topl.quivr.runtime.DynamicContext
import co.topl.common.{Digest, Preimage}

object QuivrService {
  type Trivial[T] = T
  // An Opinionated Verification Context
  val ctx: DynamicContext[Trivial, String] = ???

  def getDigestProposition(digest: Digest): Trivial[Proposition] =
    Proposer.digestProposer[Trivial, (String, Digest)].propose(("temp", digest))

  def getDigestProof(preImage: Preimage, message: SignableTxBytes): Trivial[Proof] = {
    val prover: Prover[Trivial, (Byte, Preimage)] = Prover.instances.proverInstance
    prover.prove((Primitive.Digest.token, preImage), message)
  }
}

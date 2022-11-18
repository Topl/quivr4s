package co.topl.brambl

import co.topl.quivr.Models.Primitive.Digest
import co.topl.quivr.{Proof, Proposer, Proposition, Prover, SignableTxBytes}
import co.topl.quivr.runtime.DynamicContext
import co.topl.common.Digests

object QuivrService {
  type Trivial[T] = T
  // An Opinionated Verification Context
  val ctx: DynamicContext[Trivial, String] = ???

  def getDigestProposition(digest: Digests.Digest): Trivial[Proposition] =
    Proposer.digestProposer[Trivial, (String, Digests.Digest)].propose(("temp", digest))

  def getDigestProof(preImage: Digests.Preimage, message: SignableTxBytes): Trivial[Proof] = {
    val prover: Prover[Trivial, (Byte, Digests.Preimage)] = Prover.instances.proverInstance
    prover.prove((Digest.token, preImage), message)
  }
}

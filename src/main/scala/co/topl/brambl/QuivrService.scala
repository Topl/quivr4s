package co.topl.brambl

import co.topl.quivr.Models.Primitive.Digest
import co.topl.quivr.{Proof, Proposer, Proposition, Prover, SignableTxBytes}
import co.topl.quivr.runtime.DynamicContext
import co.topl.common.Digests

object QuivrService {
  // An Opinionated Verification Context
  val ctx: DynamicContext[Option, String] = ???

  def getDigestProposition(digest: Digests.Digest): Option[Proposition] =
    Proposer.digestProposer[Option, (String, Digests.Digest)].propose(("temp", digest))

  def getDigestProof(preImage: Digests.Preimage, message: SignableTxBytes): Option[Proof] = {
    val prover: Prover[Option, (Byte, Digests.Preimage)] = Prover.instances.proverInstance
    prover.prove((Digest.token, preImage), message)
  }
}

package co.topl.brambl

import co.topl.quivr.Models.Primitive.Digest
import co.topl.quivr.{Proof, Proposer, Proposition, Prover, SignableTxBytes, User, Verifier}
import co.topl.quivr.runtime.DynamicContext

object QuivrService {
  // An Opinionated Verification Context
  val ctx: DynamicContext[Option, String] = ???

  def getDigestProposition(digest: User.Digests.Digest): Option[Proposition] =
    Proposer.digestProposer[Option, (String, User.Digest)].propose(("temp", digest))

  def getDigestProof(preImage: User.Digests.Preimage, message: SignableTxBytes): Option[Proof] = {
    val prover: Prover[Option, (Byte, User.Preimage)] = Prover.instances.proverInstance
    prover.prove((Digest.token, preImage), message)
  }
}

package co.topl.brambl

import co.topl.brambl.Models.SigningKey
import co.topl.brambl.digests.Hash
import co.topl.brambl.signatures.Signing
import co.topl.common.Data
import co.topl.common.Models.{Digest, Preimage, VerificationKey}
import co.topl.quivr.Models.{Contextual, Primitive}
import co.topl.quivr.api.{Proposer, Prover}
import co.topl.quivr.SignableBytes

// Easy to use Topl-opinionated layer for Brambl to use to access the un-opinionated quivr API

object QuivrService {

  def lockedProposition: Option[Primitive.Locked.Proposition] =
    Proposer.LockedProposer[Option, Option[Data]].propose(None)
  def lockedProof(msg: SignableBytes): Option[Primitive.Locked.Proof] =
    Prover.lockedProver[Option].prove((), msg)

  def digestProposition(preimage: Preimage, routine: Hash):
  Option[Primitive.Digest.Proposition] =
    Proposer.digestProposer[Option, (String, Digest)].propose((routine.routine, routine.hash(preimage)))

  def digestProof(msg: SignableBytes, preimage: Preimage): Option[Primitive.Digest.Proof] =
    Prover.digestProver[Option].prove(preimage, msg)

  // Hardcoding "curve25519"
  def signatureProposition(vk: VerificationKey, routine: Signing): Option[Primitive.DigitalSignature.Proposition] =
    Proposer.signatureProposer[Option, (String, VerificationKey)].propose((routine.routine, vk))
  def signatureProof(msg: SignableBytes, sk: SigningKey, routine: Signing):
  Option[Primitive.DigitalSignature.Proof] = Prover.signatureProver[Option].prove(routine.sign(sk, msg), msg)

  def heightProposition(min: Long, max: Long, chain: String = "header"): Option[Contextual.HeightRange.Proposition] =
    Proposer.heightProposer[Option, (String, Long, Long)].propose((chain, min, max))
  def heightProof(msg: SignableBytes): Option[Contextual.HeightRange.Proof] =
    Prover.heightProver[Option].prove((), msg)

  def tickProposition(min: Long, max: Long): Option[Contextual.TickRange.Proposition] =
    Proposer.tickProposer[Option, (Long, Long)].propose((min, max))
  def tickProof(msg: SignableBytes): Option[Contextual.TickRange.Proof] =
    Prover.tickProver[Option].prove((), msg)

}

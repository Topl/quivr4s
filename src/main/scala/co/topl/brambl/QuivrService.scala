package co.topl.brambl

import co.topl.brambl.Models.SigningKey
import co.topl.brambl.digests.Hash
import co.topl.brambl.signatures.Signing
import quivr.models._
import co.topl.quivr.api.{Proposer, Prover}

// Easy to use Topl-opinionated layer for Brambl to use to access the un-opinionated quivr API

object QuivrService {

  def lockedProposition: Option[Proposition] =
    Proposer.LockedProposer[Option].propose(None)

  def lockedProof(msg: SignableBytes): Option[Proof] =
    Prover.lockedProver[Option].prove((), msg)

  def digestProposition(preimage: Preimage, routine: Hash): Option[Proposition] =
    Proposer.digestProposer[Option].propose((routine.routine, routine.hash(preimage)))

  def digestProof(msg: SignableBytes, preimage: Preimage): Option[Proof] =
    Prover.digestProver[Option].prove(preimage, msg)

  // Hardcoding "curve25519"
  def signatureProposition(vk: VerificationKey, routine: Signing): Option[Proposition] =
    Proposer.signatureProposer[Option].propose((routine.routine, vk))

  def signatureProof(msg: SignableBytes, sk: SigningKey, routine: Signing): Option[Proof] =
    Prover.signatureProver[Option].prove(routine.sign(sk, msg), msg)

  def heightProposition(min: Long, max: Long, chain: String = "header"): Option[Proposition] =
    Proposer.heightProposer[Option].propose((chain, min, max))

  def heightProof(msg: SignableBytes): Option[Proof] =
    Prover.heightProver[Option].prove((), msg)

  def tickProposition(min: Long, max: Long): Option[Proposition] =
    Proposer.tickProposer[Option].propose((min, max))

  def tickProof(msg: SignableBytes): Option[Proof] =
    Prover.tickProver[Option].prove((), msg)

}

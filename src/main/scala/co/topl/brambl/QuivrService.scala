package co.topl.brambl

import cats.Id
import co.topl.brambl.routines.digests.Hash
import co.topl.brambl.routines.signatures.Signing
import quivr.models._
import co.topl.quivr.api.{Proposer, Prover}

// Easy to use Topl-opinionated layer for Brambl to use to access the un-opinionated quivr API

object QuivrService {

  def lockedProposition: Id[Proposition] =
    Proposer.LockedProposer[Id].propose(None)

  def lockedProof(msg: SignableBytes): Id[Proof] =
    Prover.lockedProver[Id].prove((), msg)

  def digestProposition(preimage: Preimage, routine: Hash): Id[Proposition] =
    Proposer.digestProposer[Id].propose((routine.routine, routine.hash(preimage)))

  def digestProof(msg: SignableBytes, preimage: Preimage): Id[Proof] =
    Prover.digestProver[Id].prove(preimage, msg)

  // Hardcoding "curve25519"
  def signatureProposition(vk: VerificationKey, routine: Signing): Id[Proposition] =
    Proposer.signatureProposer[Id].propose((routine.routine, vk))

  def signatureProof(msg: SignableBytes, sk: SigningKey, routine: Signing): Id[Proof] =
    Prover.signatureProver[Id].prove(routine.sign(sk, msg), msg)

  def heightProposition(min: Long, max: Long, chain: String = "header"): Id[Proposition] =
    Proposer.heightProposer[Id].propose((chain, min, max))

  def heightProof(msg: SignableBytes): Id[Proof] =
    Prover.heightProver[Id].prove((), msg)

  def tickProposition(min: Long, max: Long): Id[Proposition] =
    Proposer.tickProposer[Id].propose((min, max))

  def tickProof(msg: SignableBytes): Id[Proof] =
    Prover.tickProver[Id].prove((), msg)

}

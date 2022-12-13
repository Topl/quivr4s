package co.topl.brambl

import co.topl.brambl.Models.SigningKey
import co.topl.common.Data
import co.topl.common.Models.{Digest, Preimage, VerificationKey, Witness}
import co.topl.crypto.{PrivateKey, signatures}
import co.topl.crypto.hash.blake2b256
import co.topl.quivr.Models.{Contextual, Primitive}
import co.topl.quivr.api.{Proposer, Prover}
import co.topl.quivr.SignableBytes

// Easy to use Topl-opinionated layer for Brambl to use to access the un-opinionated quivr API

object QuivrService {

  def lockedProposition: Option[Primitive.Locked.Proposition] =
    Proposer.LockedProposer[Option, Option[Data]].propose(None)
  def lockedProof(msg: SignableBytes): Option[Primitive.Locked.Proof] =
    Prover.lockedProver[Option].prove((), msg)

  // Hardcoding "blake2b256"
  def digestProposition(preimage: Preimage): Option[Primitive.Digest.Proposition] = {
    val digest: Digest = Digest(blake2b256.hash(preimage.input ++ preimage.salt).value)
    Proposer.digestProposer[Option, (String, Digest)].propose(("blake2b256", digest))
  }

  def digestProof(msg: SignableBytes, preimage: Preimage): Option[Primitive.Digest.Proof] =
    Prover.digestProver[Option].prove(preimage, msg)

  // Hardcoding "curve25519"
  def signatureProposition(vk: VerificationKey): Option[Primitive.DigitalSignature.Proposition] =
    Proposer.signatureProposer[Option, (String, VerificationKey)].propose(("curve25519", vk))
  def signatureProof(msg: SignableBytes, sk: SigningKey): Option[Primitive.DigitalSignature.Proof] = {
    val witness: Witness = Witness(signatures.Curve25519.sign(PrivateKey(sk.value), msg).value)
    Prover.signatureProver[Option].prove(witness, msg)
  }

  def heightProposition(min: Long, max: Long, chain: String = "header"): Option[Contextual.HeightRange.Proposition] =
    Proposer.heightProposer[Option, (String, Long, Long)].propose((chain, min, max))
  def heightProof(msg: SignableBytes): Option[Contextual.HeightRange.Proof] =
    Prover.heightProver[Option].prove((), msg)

  def tickProposition(min: Long, max: Long): Option[Contextual.TickRange.Proposition] =
    Proposer.tickProposer[Option, (Long, Long)].propose((min, max))
  def tickProof(msg: SignableBytes): Option[Contextual.TickRange.Proof] =
    Prover.tickProver[Option].prove((), msg)

}

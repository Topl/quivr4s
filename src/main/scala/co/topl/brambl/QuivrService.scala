package co.topl.brambl

import co.topl.crypto.hash.blake2b256
import co.topl.node.TetraDatums
import co.topl.quivr.Models.Primitive
import co.topl.quivr.algebras.DigestVerifier
import co.topl.quivr.runtime.{Datum, DynamicContext}
import co.topl.quivr.{Proof, Proposition, SignableBytes, runtime}

case class DigestError() extends runtime.Error

object QuivrService {
  type Trivial[T] = T

  private case class DigestValidator() extends DigestVerifier[Trivial] {
    override def validate(v: DigestVerification): Either[DigestError, DigestVerification] = {
      val test = blake2b256.hash(v.preimage.input ++ v.preimage.salt).value
      val expected = v.digest.value

      if (expected sameElements test) Right(v)
      else Left(DigestError())
    }
  }

  // An Opinionated Verification Context
  case class ToplContext(tx: Signable) extends DynamicContext[Trivial, String] {
    override val datums: Map[String, Datum] = Map(
      // Datums with height => Not needed for digest operations
      "eon" -> TetraDatums.Eon(10, 2), // <- not sure what "beginSlot" is referring to. First slot of the eon?
      "era" -> TetraDatums.Era(22, 4),
      "epoch" -> TetraDatums.Epoch(34, 6),
      "header" -> TetraDatums.Header(24, None),
      "body" -> TetraDatums.Body(Array(0: Byte), Array(0: Byte))
    )
    override val hashingRoutines = Map("blake2b256" -> DigestValidator())
    override def signableBytes: SignableBytes = tx.getSignableBytes

    // Arbitrary values
    override val interfaces = Map()
    override val signingRoutines = Map()
    override def currentTick: Long = 250L

  }

//  val ctx: Signable => ToplContext = ToplContext
//
//  /***
//   * Return a Proposition for a Digest Operation
//   * @param digest: The digest of the Digest Proposition
//   * @return: The Digest Proposition
//   */
//  def getDigestProposition(digest: Digest): Trivial[Proposition] =
//    Proposer.digestProposer[Trivial, (String, Digest)].propose(("blake2b256", digest))
//
//
//  /***
//   * Return a Proposition for a Digest Operation from a preimage
//   *
//   * This function allows the creation of a Digest Proposition with the Preimage.
//   * It is included here as it may be how we want Digest Propositions to be initialized in the actual implementation.
//   *
//   * In other words, the value of this function depends on if the user will have access to the preimage or digest
//   * when creating a Digest Proposition
//   *
//   * @param preimage: The Preimage to be hashed into the digest in the Digest Proposition
//   * @return: The Digest Proposition
//   */
//  def getDigestProposition(preimage: Preimage): Trivial[Proposition] = {
//    val digest = Digest(blake2b256.hash(preimage.input ++ preimage.salt).value)
//    getDigestProposition(digest)
//  }
//
//
//  /***
//   * Return a Proof for a Digest operation.
//   * @param preImage: The preImage of the Digest Proof
//   * @param message: A message to bind with the proof
//   * @return: The Digest Proof
//   */
//  def getDigestProof(preImage: Preimage, message: SignableBytes): Trivial[Proof] = {
//    val prover: Prover[Trivial, (Byte, Preimage)] = Prover.instances.proverInstance
//    prover.prove((Primitive.Digest.token, preImage), message)
//  }
//
//  /***
//   * Verify that a proof satisfies a proposition in an opinionated Topl context.
//   * @param proposition: The Proposition we are trying to satisfy
//   * @param proof: The Proof to verify
//   * @param tx: A Signable object to bind to the Topl context. Most likely the containing transaction
//   * @return Boolean
//   */
//  def verify(proposition: Option[Proposition], proof: Option[Proof], tx: Signable): Trivial[Boolean] = {
//    val verifier: Verifier[Trivial] = Verifier.instances.verifierInstance
//
//    if(proposition.isEmpty || proof.isEmpty) false
//    else verifier.evaluate(proposition, proof, ctx(tx))
//  }
//
//  // I'm not sure where the following 2 functions should go.
//
//  /***
//   * Verify that an attestation is satisfied
//   *
//   * @param attestation: Attestation to verify
//   * @param tx: The transaction the attestation belongs to.
//   * @return Boolean
//   */
//  private def verifyAttestation(attestation: TetraDatums.Attestation)(implicit tx: TetraDatums.IoTx): Boolean = {
//    val threshold = attestation.image.threshold
//    val numSatisfied = (attestation.known.conditions zip attestation.responses)
//      .map(challenges => verify(challenges._1, challenges._2, tx): Int)
//      .sum
//
//    threshold >= numSatisfied
//  }
//
//  /***
//   * Verify that all the attestations in a transaction is verified
//   * @param tx: The transaction to verify
//   * @return Boolean
//   */
//  def verifyIoTx(implicit tx: TetraDatums.IoTx): Boolean =
//    tx.inputs.map(_.attestation)
//      .map(verifyAttestation)
//      .reduce(_ && _) // Only true iff all attestations are true
//
//}

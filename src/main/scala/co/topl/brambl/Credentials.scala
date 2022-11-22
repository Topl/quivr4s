package co.topl.brambl

import co.topl.brambl.Models._
import co.topl.node.Tetra
import co.topl.brambl.Storage._
import co.topl.quivr.SignableBytes


// JAA - Credentials should have the single job of taking an unproven transaction and converting it to a proven one. Nothing else should be exposed I think

object Credentials {
  /***
   * Prove an unproven input containing a 1 of 1 Digest operation
   * @param unprovenInput: Unproven input that contains Proposition.Known
   * @param message: Message to bind with the proofs
   * @return
   */
  private def proveSpentOutputV1(unprovenInput: UnprovenSpentOutputV1, message: SignableBytes): Tetra.IoTransaction.SpentOutput = {
    val predicateImage = getBoxById(unprovenInput.reference).image
    val preImage = getDigestPreImage(getIndicesByBoxId(unprovenInput.reference))
    val proof = Option(QuivrService.getDigestProof(preImage, message))
    val attestation = Tetra.Attestation(predicateImage, unprovenInput.knownPredicate, List(proof))

    Tetra.IoTransaction.SpentOutput(
      unprovenInput.reference,
      attestation,
      unprovenInput.value,
      unprovenInput.datum
    )
  }

  /***
   * Prove an unproven input containing a 1 of 1 Digest operation
   * @param unprovenInput: Unproven input that does not contain Proposition.Known
   * @param message: Message to bind with the proofs
   * @return
   */
  private def proveSpentOutputV2(unprovenInput: UnprovenSpentOutputV2, message: SignableBytes): Tetra.IoTransaction.SpentOutput = {
    val predicateImage = getBoxById(unprovenInput.reference).image
    val idx = getIndicesByBoxId(unprovenInput.reference)
    val preImage = getDigestPreImage(idx)
    val proof = Option(QuivrService.getDigestProof(preImage, message))
    val knownPredicate = List(Option(QuivrService.getDigestProposition(preImage)))
    val attestation = Tetra.Attestation(predicateImage, Tetra.Predicate.Known(knownPredicate), List(proof))

    Tetra.IoTransaction.SpentOutput(
      unprovenInput.reference,
      attestation,
      unprovenInput.value,
      unprovenInput.datum
    )
  }

  /***
   * Prove an unproven transaction.
   * @param unprovenTx
   * @return
   */
  def proveIoTx[T](unprovenTx: UnprovenIoTx[T]): Tetra.IoTransaction = {
    val message = unprovenTx.getSignableBytes

    val unprovenInput = unprovenTx.inputs.head

    val provenInput = unprovenInput match {
      case v1: UnprovenSpentOutputV1 => proveSpentOutputV1(v1, message)
      case v2: UnprovenSpentOutputV2 => proveSpentOutputV2(v2, message)
    }

    Tetra.IoTransaction(List(provenInput), unprovenTx.outputs, unprovenTx.schedule, unprovenTx.metadata)
  }

}

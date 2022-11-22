package co.topl.brambl

import co.topl.node.Models.SignableBytes
import co.topl.brambl.Models._
import co.topl.node.Tetra
import co.topl.brambl.Storage._


// JAA - Credentials should have the single job of taking an unproven transaction and converting it to a proven one. Nothing else should be exposed I think

object Credentials {
  // prove an unproven input (digest proof). indices version
  private def proveSpentOutputV1(unprovenInput: UnprovenSpentOutputV1, message: SignableBytes): Tetra.IoTx.SpentOutput = {
    val predicateImage = getBoxById(unprovenInput.reference).image
    val preImage = getDigestPreImage(getIndicesByBoxId(unprovenInput.reference))
    val proof = Option(QuivrService.getDigestProof(preImage, message))
    val attestation = Tetra.Attestation(predicateImage, unprovenInput.knownPredicate, List(proof))

    Tetra.IoTx.SpentOutput(
      unprovenInput.reference,
      attestation,
      unprovenInput.value,
      unprovenInput.datum
    )
  }

  // Prove an unproven transaction. indices version
  def proveIoTxV1(unprovenTx: UnprovenIoTxV1): Tetra.IoTx = {
    val message = unprovenTx.getSignableBytes

    val unprovenInput = unprovenTx.inputs.head
    val provenInput = proveSpentOutputV1(unprovenInput, message)

    Tetra.IoTx(List(provenInput), unprovenTx.outputs, unprovenTx.schedule, unprovenTx.metadata)
  }

  // prove an unproven input (digest proof). txo version
  private def proveSpentOutputV2(unprovenInput: UnprovenSpentOutputV2, message: SignableBytes): Tetra.IoTx.SpentOutput = {
    val predicateImage = getBoxById(unprovenInput.reference).image
    val idx = getIndicesByBoxId(unprovenInput.reference)
    val preImage = getDigestPreImage(idx)
    val proof = Option(QuivrService.getDigestProof(preImage, message))
    val knownPredicate = List(Option(QuivrService.getDigestProposition(getDigest(idx))))
    val attestation = Tetra.Attestation(predicateImage, Tetra.Predicate.Known(knownPredicate), List(proof))

    Tetra.IoTx.SpentOutput(
      unprovenInput.reference,
      attestation,
      unprovenInput.value,
      unprovenInput.datum
    )
  }

  // Prove an unproven transaction. txo version
  def proveIoTxV2(unprovenTx: UnprovenIoTxV2): Tetra.IoTx = {
    val message = unprovenTx.getSignableBytes

    val unprovenInput = unprovenTx.inputs.head
    val provenInput = proveSpentOutputV2(unprovenInput, message)

    Tetra.IoTx(List(provenInput), unprovenTx.outputs, unprovenTx.schedule, unprovenTx.metadata)
  }

}

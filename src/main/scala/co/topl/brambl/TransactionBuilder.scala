package co.topl.brambl

import co.topl.node.Models.{Metadata, SignableBytes}
import co.topl.node.Tetra
import co.topl.brambl.QuivrService
import co.topl.node.Tetra.Predicate

case class UnprovenSpentOutput(
                                reference: Tetra.Box.Id,
                                knownPredicate: Tetra.Predicate.Known,
                                value: Tetra.Box.Value,
                                datum: Tetra.Datums.SpentOutput
                              )

case class UnprovenIoTx(inputs:   List[UnprovenSpentOutput],
                outputs:  List[Tetra.IoTx.UnspentOutput],
                schedule: Tetra.IoTx.Schedule,
                metadata: Metadata
               )


// Functions related to Transaction Builder
// These functions are based off of the Transaction Diagram in TSDK-173
object TransactionBuilder {
  // Create unproven transaction
  // A simple 1 input 1 output transaction
  // All propositions are digest propositions
  def buildUnprovenTransaction(
                                  input: Indices,
                                  inputMeta: Tetra.Datums.SpentOutput,
                                  output: Indices,
                                  outputMeta: Tetra.Datums.UnspentOutput,
                                  outputValue: Tetra.Box.Value,
                                  schedule: Tetra.IoTx.Schedule,
                                  metadata: Metadata
                                ): UnprovenIoTx  = {
    val unprovenInput = UnprovenSpentOutput(
      Credentials.getBoxId(input),
//      Credentials.getKnownPredicate(input),
      Predicate.Known(
        List(
          QuivrService.getDigestProposition(Credentials.getDigest(input))
        )
      ),
      Credentials.getBox(input).value,
      inputMeta
    )
    val inputs = List(unprovenInput)

    // Should go from indices => proposition => predicate => predicate Id
    // Unclear how the propositions will map to the predicate id.
    val outputAddress: Tetra.Address = ???

    val unspentOutput = Tetra.IoTx.UnspentOutput(
      outputAddress,
      outputValue,
      outputMeta
    )
    val outputs = List(unspentOutput)
    UnprovenIoTx(inputs, outputs, schedule, metadata)
  }

  // should not go here, I will move
  def getSignableBytes(unprovenTx: UnprovenIoTx): SignableBytes = ???

  // Should not go here. I will move
  def proveTransaction(unprovenTx: UnprovenIoTx): Tetra.IoTx = {
    val message = getSignableBytes(unprovenTx)
    val unprovenInput = unprovenTx.inputs.head
    val attestation = Tetra.Attestation(
      Storage.getBoxById(unprovenInput.reference).image,
      unprovenInput.knownPredicate,
      List(
        QuivrService.getDigestProof(
          Credentials.getDigestPreImage(
            Storage.getIndicesByBoxId(unprovenInput.reference)
          ),
          message
        )
      )
    )
    val provenInput = Tetra.IoTx.SpentOutput(
      unprovenInput.reference,
      attestation,
      value = unprovenInput.value,
      datum = unprovenInput.datum
    )
    Tetra.IoTx(List(provenInput), unprovenTx.outputs, unprovenTx.schedule, unprovenTx.metadata)
  }
}
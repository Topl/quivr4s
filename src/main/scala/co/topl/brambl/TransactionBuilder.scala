package co.topl.brambl

import co.topl.node.Models.Metadata
import co.topl.node.Tetra
import co.topl.node.Tetra.Predicate
import co.topl.brambl.Models._
import co.topl.brambl.Credentials._

// Functions related to Transaction Builder
object TransactionBuilder {

  // 1 of 1 proposition input
  def buildUnprovenInput(idx: Indices, meta: Tetra.Datums.SpentOutput): UnprovenSpentOutput = {
    val proposition = Option(QuivrService.getDigestProposition(getDigest(idx)))
    val knownPredicate = Predicate.Known(List(proposition))

    UnprovenSpentOutput(getBoxId(idx), knownPredicate, getBox(idx).value, meta)
  }

  // 1 of 1 proposition output
  def buildOutput(idx: Indices, value: Tetra.Box.Value, meta: Tetra.Datums.UnspentOutput): Tetra.IoTx.UnspentOutput = {
    val proposition = QuivrService.getDigestProposition(getDigest(idx))
    val predicate = Tetra.Predicate(List(proposition), 1)

    Tetra.IoTx.UnspentOutput(predicate.image.generateAddress, value, meta)
  }

  /**
   * Create unproven transaction
   * A simple 1 input 1 output transaction
   * All propositions are digest propositions
   * */
  def buildUnprovenTransaction(
                                  input: Indices,
                                  output: Indices,
                                  outputValue: Tetra.Box.Value,
                                  inputMeta: Tetra.Datums.SpentOutput = Tetra.Datums.SpentOutput(None),
                                  outputMeta: Tetra.Datums.UnspentOutput = Tetra.Datums.UnspentOutput(None),
                                  schedule: Tetra.IoTx.Schedule = Tetra.IoTx.Schedule(0, 0, 0),
                                  metadata: Metadata = None
                                ): UnprovenIoTx  = {
    val inputs = List(buildUnprovenInput(input, inputMeta))
    val outputs = List(buildOutput(output, outputValue, outputMeta))

    UnprovenIoTx(inputs, outputs, schedule, metadata)
  }
}
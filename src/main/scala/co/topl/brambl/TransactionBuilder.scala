package co.topl.brambl

import co.topl.node.Models.Metadata
import co.topl.node.Tetra
import co.topl.node.Tetra.Predicate
import co.topl.brambl.Models._
import co.topl.brambl.Storage._
import co.topl.genus.Models.Txo

// Functions related to Transaction Builder
object TransactionBuilder {

  // 1 of 1 proposition input. indices version
  def buildUnprovenSpentOutputV1(idx: Indices, meta: Tetra.Datums.SpentOutput): UnprovenSpentOutputV1 = {
    val proposition = Option(QuivrService.getDigestProposition(getDigest(idx)))
    val knownPredicate = Predicate.Known(List(proposition))

    val txo = getTxo(idx)

    UnprovenSpentOutputV1(txo.id, knownPredicate, txo.box.value, meta)
  }

  // 1 of 1 proposition input. txo version
  def buildUnprovenSpentOutputV2(txo: Txo, meta: Tetra.Datums.SpentOutput): UnprovenSpentOutputV2 =
    UnprovenSpentOutputV2(txo.id, txo.box.value, meta)

  // 1 of 1 proposition output
  def buildUnspentOutput(idx: Indices, value: Tetra.Box.Value, meta: Tetra.Datums.UnspentOutput): Tetra.IoTx.UnspentOutput = {
    val proposition = QuivrService.getDigestProposition(getDigest(idx))
    val predicate = Tetra.Predicate(List(proposition), 1)

    Tetra.IoTx.UnspentOutput(predicate.image.generateAddress, value, meta)
  }

  /**
   * Create unproven transaction. indices version
   * A simple 1 input 1 output transaction
   * All propositions are digest propositions
   * */
  def buildUnprovenIoTxV1(
                                  input: Indices,
                                  output: Indices,
                                  outputValue: Tetra.Box.Value,
                                  inputMeta: Tetra.Datums.SpentOutput = Tetra.Datums.SpentOutput(None),
                                  outputMeta: Tetra.Datums.UnspentOutput = Tetra.Datums.UnspentOutput(None),
                                  schedule: Tetra.IoTx.Schedule = Tetra.IoTx.Schedule(0, 0, 0),
                                  metadata: Metadata = None
                                ): UnprovenIoTxV1  = {
    val inputs = List(buildUnprovenSpentOutputV1(input, inputMeta))
    val outputs = List(buildUnspentOutput(output, outputValue, outputMeta))

    UnprovenIoTxV1(inputs, outputs, schedule, metadata)
  }

  /**
   * Create unproven transaction. txo version
   * A simple 1 input 1 output transaction
   * All propositions are digest propositions
   * */
  def buildUnprovenIoTxV2(
                           input: Txo,
                           outputValue: Tetra.Box.Value,
                           inputMeta: Tetra.Datums.SpentOutput = Tetra.Datums.SpentOutput(None),
                           outputMeta: Tetra.Datums.UnspentOutput = Tetra.Datums.UnspentOutput(None),
                           schedule: Tetra.IoTx.Schedule = Tetra.IoTx.Schedule(0, 0, 0),
                           metadata: Metadata = None
                         ): UnprovenIoTxV2  = {
    val inputs = List(buildUnprovenSpentOutputV2(input, inputMeta))
    val outputs = List(buildUnspentOutput(getNextIndices, outputValue, outputMeta))

    UnprovenIoTxV2(inputs, outputs, schedule, metadata)
  }
}
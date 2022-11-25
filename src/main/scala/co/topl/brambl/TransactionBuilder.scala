package co.topl.brambl

import co.topl.node.Models.Metadata
import co.topl.node.TetraDatums
import co.topl.node.TetraDatums.Predicate
import co.topl.brambl.Models._
import co.topl.brambl.Storage._
import .TransactionOutput

// Functions related to Transaction Builder
object TransactionBuilder {
  /**
   * Create a simple unproven input
   *
   * Version where unproven input contains Predicate.Known
   *
   * Predicate will be a 1 of 1 Digest
   * */
  private def buildUnprovenSpentOutputV1(idx: Indices, meta: TetraDatums.Datums.SpentOutput): UnprovenSpentOutputV1 = {
    val proposition = Option(QuivrService.getDigestProposition(getDigest(idx))) // or could be done with preimage
    val knownPredicate = Predicate.Known(List(proposition))

    val txo = getTxo(idx)

    UnprovenSpentOutputV1(txo.id, knownPredicate, txo.box.value, meta)
  }

  /**
   * Create a simple unproven input
   *
   * Version where unproven input does not contain Predicate.Known
   * */
  private def buildUnprovenSpentOutputV2(txo: TransactionOutput, meta: TetraDatums.Datums.SpentOutput): UnprovenSpentOutputV2 =
    UnprovenSpentOutputV2(txo.id, txo.box.value, meta)


  /**
   * Create a simple output whose predicate is a 1 of 1 Digest
   *
   * In addition to the indices being used to generate the digest secret, the indices will be used to
   * store the created proposition
   * */
  private def buildUnspentOutput(idx: Indices, value: TetraDatums.Box.Value, meta: TetraDatums.Datums.UnspentOutput): TetraDatums.IoTx.UnspentOutput = {
    val proposition = QuivrService.getDigestProposition(getDigest(idx)) // or could be done with preimage
    val predicate = TetraDatums.Predicate(List(proposition), 1)

    TetraDatums.IoTx.UnspentOutput(predicate.image.generateAddress, value, meta)
  }

  /**
   * Create unproven transaction.
   * A simple 1 input 1 output transaction where all propositions are digest propositions
   *
   * indices version (i.e, input and output params are specified as indices)
   * */
  def buildUnprovenIoTxV1(
                           input: Indices,
                           output: Indices,
                           outputValue: TetraDatums.Box.Value,
                           inputMeta: TetraDatums.Datums.SpentOutput = TetraDatums.Datums.SpentOutput(None),
                           outputMeta: TetraDatums.Datums.UnspentOutput = TetraDatums.Datums.UnspentOutput(None),
                           schedule: TetraDatums.IoTx.Schedule = TetraDatums.IoTx.Schedule(0, 0, 0),
                           metadata: Metadata = None
                                ): UnprovenIoTx[UnprovenSpentOutputV1]  = {
    val inputs = List(buildUnprovenSpentOutputV1(input, inputMeta))
    val outputs = List(buildUnspentOutput(output, outputValue, outputMeta))

    UnprovenIoTx[UnprovenSpentOutputV1](inputs, outputs, schedule, metadata)
  }

  /**
   * Create unproven transaction.
   * A simple 1 input 1 output transaction where all propositions are digest propositions
   *
   * Txo version (i.e, input is specified as Genus Txo)
   * */
  def buildUnprovenIoTxV2(
                           input: TransactionOutput,
                           outputValue: TetraDatums.Box.Value,
                           inputMeta: TetraDatums.Datums.SpentOutput = TetraDatums.Datums.SpentOutput(None),
                           outputMeta: TetraDatums.Datums.UnspentOutput = TetraDatums.Datums.UnspentOutput(None),
                           schedule: TetraDatums.IoTx.Schedule = TetraDatums.IoTx.Schedule(0, 0, 0),
                           metadata: Metadata = None
                         ): UnprovenIoTx[UnprovenSpentOutputV2]  = {
    val inputs = List(buildUnprovenSpentOutputV2(input, inputMeta))
    val outputs = List(buildUnspentOutput(getNextIndices, outputValue, outputMeta))

    UnprovenIoTx[UnprovenSpentOutputV2](inputs, outputs, schedule, metadata)
  }

}
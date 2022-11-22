package co.topl.brambl

import co.topl.node.Tetra
import co.topl.node.Tetra.Predicate
import co.topl.brambl.Models._
import co.topl.brambl.Storage._
import co.topl.genus.Models.Txo

// Functions related to Transaction Builder
object TransactionBuilder {
  /**
   * Create a simple unproven input
   *
   * Version where unproven input contains Predicate.Known
   *
   * Predicate will be a 1 of 1 Digest
   * */
  private def buildUnprovenSpentOutputV1(idx: Indices, meta: Tetra.Datums.SpentOutput): UnprovenSpentOutputV1 = {
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
  private def buildUnprovenSpentOutputV2(txo: Txo, meta: Tetra.Datums.SpentOutput): UnprovenSpentOutputV2 =
    UnprovenSpentOutputV2(txo.id, txo.box.value, meta)


  /**
   * Create a simple output whose predicate is a 1 of 1 Digest
   *
   * In addition to the indices being used to generate the digest secret, the indices will be used to
   * store the created proposition
   * */
  private def buildUnspentOutput(idx: Indices, value: Tetra.Box.Value, meta: Tetra.Datums.UnspentOutput): Tetra.IoTransaction.UnspentOutput = {
    val proposition = QuivrService.getDigestProposition(getDigest(idx)) // or could be done with preimage
    val predicate = Tetra.Predicate(List(proposition), 1)

    Tetra.IoTransaction.UnspentOutput(predicate.image.generateAddress, value, meta)
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
                                  outputValue: Tetra.Box.Value,
                                  inputMeta: Tetra.Datums.SpentOutput = Tetra.Datums.SpentOutput(Array()),
                                  outputMeta: Tetra.Datums.UnspentOutput = Tetra.Datums.UnspentOutput(Array()),
                                  datum: Tetra.Datums.IoTx = Tetra.Datums.IoTx(
                                    Tetra.IoTransaction.Schedule(0, 0, 0),
                                    Tetra.Blob.Id(Array()),
                                    Array()
                                  ),
                                  metadata: Option[Tetra.Blob] = None
                                ): UnprovenIoTransaction[UnprovenSpentOutputV1]  = {
    val inputs = List(buildUnprovenSpentOutputV1(input, inputMeta))
    val outputs = List(buildUnspentOutput(output, outputValue, outputMeta))

    UnprovenIoTransaction[UnprovenSpentOutputV1](inputs, outputs, datum, metadata)
  }

  /**
   * Create unproven transaction.
   * A simple 1 input 1 output transaction where all propositions are digest propositions
   *
   * Txo version (i.e, input is specified as Genus Txo)
   * */
  def buildUnprovenIoTxV2(
                           input: Txo,
                           outputValue: Tetra.Box.Value,
                           inputMeta: Tetra.Datums.SpentOutput = Tetra.Datums.SpentOutput(Array()),
                           outputMeta: Tetra.Datums.UnspentOutput = Tetra.Datums.UnspentOutput(Array()),
                           datum: Tetra.Datums.IoTx = Tetra.Datums.IoTx(
                             Tetra.IoTransaction.Schedule(0, 0, 0),
                             Tetra.Blob.Id(Array()),
                             Array()
                           ),
                           metadata: Option[Tetra.Blob] = None
                         ): UnprovenIoTransaction[UnprovenSpentOutputV2]  = {
    val inputs = List(buildUnprovenSpentOutputV2(input, inputMeta))
    val outputs = List(buildUnspentOutput(getNextIndices, outputValue, outputMeta))

    UnprovenIoTransaction[UnprovenSpentOutputV2](inputs, outputs, datum, metadata)
  }

}
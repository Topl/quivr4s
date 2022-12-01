package co.topl.brambl

import co.topl.node.box.{Lock, Locks, Values}
import co.topl.node.transaction.{Datums, IoTransaction, SpentOutput, UnspentOutput, Attestations}
import co.topl.node.{Address, Events, Identifiers}
import co.topl.quivr.runtime.Datum
import co.topl.node.References


// Create un-proven transactions

// For now it create hardcoded simple transaction
// => A 1 input 1 output Tx with 1 of 1 digest predicate

object TransactionBuilder {

  // Construct input. Mostly hardcoded for now
  private def constructSpentOutput(reference: References.KnownSpendable32): SpentOutput = {
    val lock = Locks.Predicate(List(QuivrService.digestProposition), 1)
    val responses = List.fill(lock.challenges.length)(None)
    val attestation = Attestations.Predicate(lock, responses)

    val value = Values.Token(1)
    val datum = DatumBuilder.constructSpentOutputDatum
    val opts = ???
    SpentOutput(reference, attestation, value, datum, opts)
  }

  // Construct output. Mostly hardcoded for now
  private def constructUnspentOutput: UnspentOutput = {
    val lock = Locks.Predicate(List(QuivrService.digestProposition), 1)
    val address = Address(0, 0, Identifiers.boxLock32(lock))
    val value = Values.Token(1)
    val datum = DatumBuilder.constructUnspentOutputDatum
    val opts = ???
    UnspentOutput(address, value, datum, opts)
  }


  // Will take in a list of Txos and more
  // For now will just hardcode
  def constructIoTransaction(refs: List[References.KnownSpendable32]): IoTransaction  = {
    val inputs = refs.map(constructSpentOutput)
    val outputs = List(constructUnspentOutput)
    val datum = DatumBuilder.constructIoTxDatum
    val opts = ??? // unsure
    IoTransaction(inputs, outputs, datum, opts)
  }
}

private object DatumBuilder {
  private def ioTxEvent: Events.IoTransaction = ???
  private def spentOutputEvent: Events.SpentOutput = ???
  private def unspentOutputEvent: Events.UnspentOutput = ???

  def constructIoTxDatum: Datum[Events.IoTransaction] = Datums.ioTransactionDatum(ioTxEvent)
  def constructSpentOutputDatum: Datum[Events.SpentOutput] = Datums.spentOutputDatum(spentOutputEvent)
  def constructUnspentOutputDatum: Datum[Events.UnspentOutput] = Datums.unspentOutputDatum(unspentOutputEvent)
}
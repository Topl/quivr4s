package co.topl.brambl

import co.topl.node.box.{Lock, Locks, Values}
import co.topl.node.transaction.{Datums, IoTransaction, SpentTransactionOutput, UnspentTransactionOutput, Attestations}
import co.topl.node.{Address, Events, Identifiers, Known}
import co.topl.quivr.runtime.Datum



// Create un-proven transactions

// For now it create hardcoded simple transaction
// => A 1 input 1 output Tx with 1 of 1 digest predicate

object TransactionBuilder {

  // Construct input. Mostly hardcoded for now
  private def constructSpentOutput(reference: Known.TransactionOutput32): SpentTransactionOutput = {
    val lock = Locks.Predicate(List(QuivrService.digestProposition(???)), 1)
    val responses = List.fill(lock.challenges.length)(None)
    val attestation = Attestations.Predicate(lock, responses)

    val value = Values.Token(1)
    val datum = DatumBuilder.constructSpentOutputDatum
    val opts = ???
    SpentTransactionOutput(reference, attestation, value, datum, opts)
  }

  // Construct output. Mostly hardcoded for now
  private def constructUnspentOutput: UnspentTransactionOutput = {
    val lock = Locks.Predicate(List(QuivrService.digestProposition(???)), 1)
    val address = Address(0, 0, Identifiers.boxLock32(lock))
    val value = Values.Token(1)
    val datum = DatumBuilder.constructUnspentOutputDatum
    val opts = ???
    UnspentTransactionOutput(address, value, datum, opts)
  }


  // Will take in a list of Txos and more
  // For now will just hardcode
  def constructIoTransaction(refs: List[Known.TransactionOutput32]): IoTransaction  = {
    val inputs = refs.map(constructSpentOutput)
    val outputs = List(constructUnspentOutput)
    val datum = DatumBuilder.constructIoTxDatum
    IoTransaction(inputs, outputs, datum)
  }
}

private object DatumBuilder {
  private def ioTxEvent: Events.IoTransaction = ???
  private def spentOutputEvent: Events.SpentTransactionOutput = ???
  private def unspentOutputEvent: Events.UnspentTransactionOutput = ???

  def constructIoTxDatum: Datum[Events.IoTransaction] = Datums.ioTransactionDatum(ioTxEvent)
  def constructSpentOutputDatum: Datum[Events.SpentTransactionOutput] = Datums.spentOutputDatum(spentOutputEvent)
  def constructUnspentOutputDatum: Datum[Events.UnspentTransactionOutput] = Datums.unspentOutputDatum(unspentOutputEvent)
}
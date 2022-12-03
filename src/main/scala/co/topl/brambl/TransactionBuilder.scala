package co.topl.brambl

import co.topl.common.Models.Digest
import co.topl.node.box.{Blob, Lock, Locks, Values}
import co.topl.node.transaction.IoTransaction.Schedule
import co.topl.node.transaction.{Attestations, Datums, IoTransaction, SpentTransactionOutput, UnspentTransactionOutput}
import co.topl.node.{Address, Events, Identifiers, KnownIdentifiers, KnownReferences, SmallData}
import co.topl.quivr.runtime.Datum
import co.topl.crypto.hash.blake2b256
import co.topl.quivr.Proposition



// Create un-proven transactions

// For now it create hardcoded simple transaction
// => A 1 input 1 output Tx with X of 2 digest and locked predicate

object TransactionBuilder {

  private def getDummyDigest(idx: Indices): Digest = {
    Digest(
      blake2b256.hash(Wallet.getSecret(idx)).value
    )
  }

  // Construct input. Mostly hardcoded for now
  def constructInput(
                      reference: KnownIdentifiers.TransactionOutput32,
                      metadata: SmallData = Array(),
                      blobs: List[Option[Blob]] = List()
                    ): SpentTransactionOutput = {

    // TODO: Figure out how to fetch a Predicate from wallet?
    // if not, make it so that you can construct 2 different inputs;first one a 1 of 2, second a 2 of 2
    val digest = getDummyDigest(Wallet.getIndicesByIdentifier(reference))
    val lock = Locks.Predicate(List(QuivrService.digestProposition(digest)), 1)

    val responses = List.fill(lock.challenges.length)(None)
    val attestation = Attestations.Predicate(lock, responses)

    val value = Values.Token(1, blobs)

    val datumRefs = KnownReferences.Predicate32(
      reference.network,
      reference.ledger,
      List(reference.index), // Why is this a list. Maybe I am misunderstanding this field
      ??? // Not sure where this should come from
    )

    val datum = DatumBuilder.constructSpentOutputDatum(
      List(datumRefs), // Why is this a list. Maybe I am misunderstanding this field
      metadata
    )
    val opts = ???
    SpentTransactionOutput(reference, attestation, value, datum, opts)
  }

  // Construct output. Mostly hardcoded for now
  // Only a Predicate lock for now
  def constructOutput(challenges: List[Proposition]): UnspentTransactionOutput = {
    val lock = Locks.Predicate(List(QuivrService.digestProposition(???)), 1)
    val address = Address(0, 0, Identifiers.boxLock32(lock))
    val value = Values.Token(1, List())
    // TODO
    val datum = DatumBuilder.constructUnspentOutputDatum(List(), Array())
    val opts = ???
    UnspentTransactionOutput(address, value, datum, opts)
  }


  // Will take in a list of Txos and more
  // For now will just hardcode
  def constructIoTransaction(
                              inputRefs: List[KnownIdentifiers.TransactionOutput32],
                              outputs: List[UnspentTransactionOutput],
                              schedule: IoTransaction.Schedule,
                              txMeta: SmallData,

                            ): IoTransaction  = {
    val inputs = inputRefs.map(constructInput(_))
    val datum = DatumBuilder.constructIoTxDatum(schedule, inputRefs, txMeta)
    IoTransaction(inputs, outputs, datum)
  }
}

private object DatumBuilder {
  def constructIoTxDatum(schedule: Schedule, refs: List[KnownIdentifiers.TransactionOutput32], metadata: SmallData): Datum[Events.IoTransaction] =
    Datums.ioTransactionDatum(
      Events.IoTransaction(
        schedule,
        refs,
        List(),
        metadata
      )
    )
  def constructSpentOutputDatum(refs: List[KnownReferences.Predicate32], metadata: SmallData): Datum[Events.SpentTransactionOutput] =
    Datums.spentOutputDatum(
      Events.SpentTransactionOutput(
        refs,
        List(),
        metadata
      )
    )
  def constructUnspentOutputDatum(refs: List[KnownReferences.Blob32], metadata: SmallData): Datum[Events.UnspentTransactionOutput] =
    Datums.unspentOutputDatum(
      Events.UnspentTransactionOutput(
        refs,
        List(),
        metadata
      )
    )
}
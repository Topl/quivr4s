package co.topl.brambl.nativeTransactor

import co.topl.brambl.wallet.Storage
import co.topl.node.box.{Lock, Locks, Values}
import co.topl.node.transaction._
import co.topl.node.{Address, Events, KnownIdentifier}
import co.topl.quivr.runtime.Datum


// Create un-proven transactions

// For now it creates the hardcoded simple transactions that are needed for the credential examples
// Although the following won't represent exactly what the transaction builder will do, it will inform what's needed

case class MockBuilder(store: Storage) extends Builder{
  /**
   * Construct an unproven input.
   *
   * @param id Known identifier for which the contents of this input is coming from
   * @return The unproven input
   */
  private def constructTestInput(id: KnownIdentifier): Option[SpentTransactionOutput] = {
    store.getBoxByKnownIdentifier(id)
      .map(box => {
        val datum: Datum[Events.SpentTransactionOutput] = Datums.spentOutputDatum(
          Events.SpentTransactionOutput(
            List(), // references does not seem necessary to credentialler
            Array() // metadata is trivial to credentialler
          )
        )
        SpentTransactionOutput(
          id,
          buildUnprovenAttestation(box.lock),
          box.value,
          datum,
          List() // opts does not seem necessary to credentialler
        )
      })
  }

  private def buildUnprovenAttestation(lock: Lock): Attestation = lock match {
    case p: Locks.Predicate => Attestations.Predicate(
      p,
      List.fill(p.challenges.length)(None) // Its unproven
    )
    case _ => ??? // Only considering Predicate locks for now
  }

  /**
   * Construct an output.
   * @param address Address of the utxo
   * @return The transaction output
   */
  private def constructTestOutput(address: Address): UnspentTransactionOutput = {
    val value = Values.Token(
      1,
      List() // blobs does not seem necessary to credentialler
    )
    val datum: Datum[Events.UnspentTransactionOutput] = Datums.unspentOutputDatum(
      Events.UnspentTransactionOutput(
        List(), // references does not seem necessary to credentialler
        Array() // metadata is trivial to credentialler
      )
    )
    val opts = List() // opts does not seem necessary to credentialler
    UnspentTransactionOutput(address, value, datum, opts)
  }

  /**
   * Construct an unproven transaction with 1 input 1 output.
   * Both input and output are N of 2 attestation with a locked and digest operation
   *
   * @param threshold Threshold of the attestation
   * @return The unproven transaction
   */
  override def constructTransaction(inputAddress: Address, outputAddress: Address): Option[IoTransaction] = {
    val datum: Datum[Events.IoTransaction] = Datums.ioTransactionDatum(
      Events.IoTransaction(
        IoTransaction.Schedule(0, 100, 9999), // Arbitrary timestamp
        List(), // references32 does not seem necessary to credentialler
        List(), // references 64 does not seem necessary to credentialler
        Array() // metadata is trivial to credentialler
      )
    )
    store.getKnownIdentifierByAddress(inputAddress)
      .flatMap(constructTestInput)
      .map(input => IoTransaction(List(input), List(constructTestOutput(outputAddress)), datum))

  }
}
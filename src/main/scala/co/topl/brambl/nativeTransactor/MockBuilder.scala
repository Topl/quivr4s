package co.topl.brambl.nativeTransactor

import cats.implicits._
import co.topl.brambl.models.Address
import co.topl.brambl.models.Datum
import co.topl.brambl.models.Event
import co.topl.brambl.models.KnownIdentifier
import co.topl.brambl.models.box.Lock
import co.topl.brambl.models.box.Value
import co.topl.brambl.models.transaction.Attestation
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.models.transaction.Schedule
import co.topl.brambl.models.transaction.SpentTransactionOutput
import co.topl.brambl.models.transaction.UnspentTransactionOutput
import co.topl.brambl.wallet.Storage
import com.google.protobuf.ByteString
import quivr.models.Int128
import quivr.models.Proof

// Create un-proven transactions

// For now it creates the hardcoded simple transactions that are needed for the credential examples
// Although the following won't represent exactly what the transaction builder will do, it will inform what's needed

case class MockBuilder(store: Storage) extends Builder {

  /**
   * Construct an unproven input.
   *
   * @param id Known identifier for which the contents of this input is coming from
   * @return The unproven input
   */
  private def constructTestInput(id: KnownIdentifier): Option[SpentTransactionOutput] =
    store
      .getBoxByKnownIdentifier(id)
      .map { box =>
        val datum: Datum.SpentOutput = Datum.SpentOutput(
          Event
            .SpentTransactionOutput(
              None // metadata is trivial to credentialler
            )
            .some
        )
        SpentTransactionOutput(
          id.some,
          box.lock.map(buildUnprovenAttestation),
          box.value,
          datum.some,
          List() // opts does not seem necessary to credentialler
        )
      }

  private def buildUnprovenAttestation(lock: Lock): Attestation = lock.value match {
    case Lock.Value.Predicate(p) =>
      Attestation().withPredicate(
        Attestation.Predicate(
          p.some,
          List.fill(p.challenges.length)(Proof()) // Its unproven
        )
      )
    case _ => ??? // Only considering Predicate locks for now
  }

  /**
   * Construct an output.
   * @param address Address of the utxo
   * @return The transaction output
   */
  private def constructTestOutput(address: Address): UnspentTransactionOutput = {
    val value = Value().withToken(
      Value.Token(
        Int128(ByteString.copyFrom(BigInt(1).toByteArray)).some
      )
    )
    val datum: Datum.UnspentOutput = Datum.UnspentOutput(
      Event
        .UnspentTransactionOutput(
          None // metadata is trivial to credentialler
        )
        .some
    )
    UnspentTransactionOutput(address.some, value.some, datum.some)
  }

  /**
   * Construct an unproven transaction with 1 input 1 output.
   * Both input and output are N of 2 attestation with a locked and digest operation
   *
   * @param threshold Threshold of the attestation
   * @return The unproven transaction
   */
  override def constructTransaction(inputAddress: Address, outputAddress: Address): Option[IoTransaction] = {
    val datum: Datum.IoTransaction = Datum.IoTransaction(
      Event
        .IoTransaction(
          Schedule(0, 100, 9999).some, // Arbitrary timestamp
          List(), // references32 does not seem necessary to credentialler
          List(), // references 64 does not seem necessary to credentialler
          None // metadata is trivial to credentialler
        )
        .some
    )
    store
      .getKnownIdentifierByAddress(inputAddress)
      .flatMap(constructTestInput)
      .map(input => IoTransaction(List(input), List(constructTestOutput(outputAddress)), datum.some))

  }
}

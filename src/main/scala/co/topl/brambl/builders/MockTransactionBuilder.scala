package co.topl.brambl.builders

import cats.implicits._
import co.topl.brambl.models.{Address, Datum, Event, Indices, KnownIdentifier}
import co.topl.brambl.models.box.Lock
import co.topl.brambl.models.box.Value
import co.topl.brambl.models.transaction.Attestation
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.models.transaction.SpentTransactionOutput
import co.topl.brambl.models.transaction.UnspentTransactionOutput
import co.topl.brambl.wallet.Storage
import com.google.protobuf.ByteString
import quivr.models.Int128
import quivr.models.Proof
import co.topl.brambl.models.Datum.{IoTransaction => IoTransactionDatum, SpentOutput => SpentOutputDatum, UnspentOutput => UnspentOutputDatum}


// Create un-proven transactions

// For now it creates the hardcoded simple transactions that are needed for the credential examples
// Although the following won't represent exactly what the transaction builder will do, it will inform what's needed

case class MockTransactionBuilder(store: Storage) extends TransactionBuilder {

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

  override def constructTransaction(
                                     inputIndices: List[Indices],
                                     inputDatums: List[Option[SpentOutputDatum]],
                                     outputIndices: List[Indices],
                                     outputDatums: List[Option[UnspentOutputDatum]],
                                     locks: List[Lock],
                                     quantities: List[Long],
                                     datum: Option[IoTransactionDatum]
                                   ): Either[BuilderError, IoTransaction] = ???
}

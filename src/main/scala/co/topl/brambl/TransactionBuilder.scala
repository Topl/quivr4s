package co.topl.brambl

import co.topl.brambl.Models.{Indices, KeyPair, SigningKey}
import co.topl.common.Models.{Preimage, VerificationKey}
import co.topl.node.box.{Locks, Values}
import co.topl.node.transaction.{Attestations, Datums, IoTransaction, SpentTransactionOutput, UnspentTransactionOutput}
import co.topl.node.{Address, Events, Identifiers}
import co.topl.quivr.runtime.Datum


// Create un-proven transactions

// For now it creates the hardcoded simple transactions that are needed for the credential examples
// Although the following won't represent exactly what the transaction builder will do, it will inform what's needed

object TransactionBuilder {

  // Helper to get N of 5 predicate. only used in constructTestInput
  // Propositions in predicate are: Locked, Digest, Signature, Height, and Tick
  private def getPredicate(threshold: Int, idx: Indices): Locks.Predicate = Locks.Predicate(
    List(
      QuivrService.lockedProposition,
      QuivrService.digestProposition(Wallet
        .getPreimage(idx)
        .getOrElse(Preimage("unsolvable preimage".getBytes, "salt".getBytes))
      ),
      QuivrService.signatureProposition(Wallet
        .getKeyPair(idx)
        .getOrElse(KeyPair(SigningKey("fake sk".getBytes), VerificationKey("fake vk".getBytes)))
        .vk
      ),
      QuivrService.heightProposition(2, 8),
      QuivrService.tickProposition(2, 8),
    ),
    threshold // N of 5 predicate
  )

  /**
   * Construct an unproven input.
   *
   * @param threshold The threshold of the input's attestation
   * @param idx   indices of the known identifier that the input will be referencing.
   *               If the wallet does not have a known identifier at these indices, then None is returned
   * @return The unproven input
   */
  private def constructTestInput(threshold: Int, idx: Indices): Option[SpentTransactionOutput] = {
    Wallet.getKnownIdentifierByIndices(idx).map {knownIdentifier =>
      val predicate = getPredicate(threshold, idx)
      val attestation = Attestations.Predicate(
        predicate,
        List.fill(predicate.challenges.length)(None) // Its unproven
      )
      val value = Values.Token(
        1,
        List() // blobs does not seem necessary to credentialler
      )
      val datum: Datum[Events.SpentTransactionOutput] = Datums.spentOutputDatum(
        Events.SpentTransactionOutput(
          List(), // references does not seem necessary to credentialler
          Array() // metadata is trivial to credentialler
        )
      )
      val opts = List() // opts does not seem necessary to credentialler
      SpentTransactionOutput(knownIdentifier, attestation, value, datum, opts)
    }
  }

  /**
   * Construct an unproven output.
   * @param threshold
   * @return The transaction output
   */
  private def constructTestOutput(threshold: Int): UnspentTransactionOutput = {
    val address = Address(0, 0, Identifiers.boxLock32(
      Locks.Predicate(List(QuivrService.lockedProposition), 1)
    ))
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
  def constructTestTransaction(threshold: Int, inputIdx: Indices): IoTransaction = {
    val datum: Datum[Events.IoTransaction] = Datums.ioTransactionDatum(
      Events.IoTransaction(
        IoTransaction.Schedule(0, 100, 9999), // Arbitrary timestamp
        List(), // references32 does not seem necessary to credentialler
        List(), // references 64 does not seem necessary to credentialler
        Array() // metadata is trivial to credentialler
      )
    )
    IoTransaction(List(constructTestInput(threshold, inputIdx)), List(constructTestOutput(threshold)), datum)
  }
}
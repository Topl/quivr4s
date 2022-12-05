/**
* A worksheet for using the Credentialler
* How the IoTransaction is created is irrelevant
* */
import cats.implicits.toShow
import co.topl.brambl.QuivrService
import co.topl.brambl.Credentials
import co.topl.brambl.Wallet
import co.topl.brambl.Models._
import co.topl.common.Models.Digest
import co.topl.node.{Address, Events, Identifiers}
import co.topl.node.box.{Locks, Values}
import co.topl.node.transaction.{Attestations, Datums, IoTransaction, SpentTransactionOutput, UnspentTransactionOutput}
import co.topl.quivr.runtime.Datum
import co.topl.crypto.hash.blake2b256

// Helper to get a digest obj from indices
def getDigest(idx: Indices): Digest = Digest(
  blake2b256.hash(Wallet.getSecret(idx)).value
)

// Helper to get N of 2 predicate
def getPredicate(threshold: Int, idx: Indices): Locks.Predicate = Locks.Predicate(
  List(QuivrService.lockedProposition, QuivrService.digestProposition(getDigest(idx))),
  threshold // N of 2 predicate
)

// helper to print a transaction
def printTxResult(tx: IoTransaction): Unit = {
  val isValidPrefix = if(QuivrService.validate(tx)) "" else "not "
  println(s"====")
  println(s"The following transaction is ${isValidPrefix}authorized")
  println(tx.show)
}


/**
 * Construct an unproven input.
 * The output is an N of 2 attestation with a locked and digest operation
 *
 * @return The unproven input
 */
def constructTestInput(threshold: Int, idx: Indices = Indices(0, 0, 0)): SpentTransactionOutput = {
  val knownIdentifier = Wallet.getKnownIdentifierByIndices(idx)
  val attestation = Attestations.Predicate(
    getPredicate(threshold, idx),
    List(None, None) // Its unproven
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

def constructTestOutput(threshold: Int, idx: Indices = Indices(0, 0, 1)): UnspentTransactionOutput = {
  val address = Address(0, 0, Identifiers.boxLock32(getPredicate(threshold, idx)))
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
def constructTestTransaction(threshold: Int): IoTransaction = {
  val datum: Datum[Events.IoTransaction] = Datums.ioTransactionDatum(
    Events.IoTransaction(
      IoTransaction.Schedule(0, 100, 9999), // Arbitrary timestamp
      List(), // references32 does not seem necessary to credentialler
      List(), // references 64 does not seem necessary to credentialler
      Array() // metadata is trivial to credentialler
    )
  )
  IoTransaction(List(constructTestInput(threshold)), List(constructTestOutput(threshold)), datum)
}

// The only difference between these 2 transaction is the output.
// Each have 1 output with a 2 proposition predicate (digest and locked).
// The first is 1 of 1 (thus provable)
// The second is 2 of 2 (thus unprovable)
val iotx1of2Unproven = constructTestTransaction(1)
val iotx2of2Unproven = constructTestTransaction(2)

// Proving transaction should be as simple as taking the unproven transaction
val iotx1of2Proven = Credentials.prove(iotx1of2Unproven)
val iotx2of2Proven = Credentials.prove(iotx2of2Unproven)

printTxResult(iotx1of2Unproven)
//printTxResult(iotx1of2Proven)
printTxResult(iotx2of2Unproven)
//printTxResult(iotx2of2Proven)
/**
* A worksheet for using the Credentialler
* How the IoTransaction is created is irrelevant
* */
import cats.implicits.toShow
import co.topl.brambl.{Context, Credentials, QuivrService, TransactionBuilder, Wallet}
import co.topl.brambl.Models._
import co.topl.common.Models.Digest
import co.topl.node.{Address, Events, Identifiers}
import co.topl.node.box.{Locks, Values}
import co.topl.node.transaction.{Attestations, Datums, IoTransaction, SpentTransactionOutput, UnspentTransactionOutput}
import co.topl.quivr.runtime.{Datum, DynamicContext}
import co.topl.crypto.hash.blake2b256
import co.topl.node.typeclasses.ContainsSignable.instances.ioTransactionSignable


def signableBytesAreNotMutated(unprovenTx: IoTransaction, provenTx: IoTransaction): Boolean = {
  val unprovenSig = ioTransactionSignable.signableBytes(unprovenTx)
  val provenSig = ioTransactionSignable.signableBytes(provenTx)
  provenSig sameElements unprovenSig
}

// Helper for manual testing
def runTest(label: String, unprovenTx: IoTransaction,
            expectedPass: Boolean, curTick: Long, headerHeight: Long): Unit = {
  println("==========================")
  println(label)
//  println(unprovenTx.show)
  val proven = Credentials.prove(unprovenTx)
//  println(proven.show)
  val bytesSame = signableBytesAreNotMutated(unprovenTx, proven)
  println(s"Signable bytes stayed the same: ${bytesSame}")
  implicit val ctx: DynamicContext[Option, String] = Context.getContext(proven, Some(curTick),
    Map("header" -> Some(Datums.headerDatum(Events.Header(headerHeight)))))
  val isAuthorized = Credentials.validate(proven)
  println(s"Is authorized? ${isAuthorized}")
  println(s"Test ${if(isAuthorized == expectedPass && bytesSame) "passed" else "failed" }")
}


// Both of these indices are registered to have a known identifier in the wallet (i.e, a previous tx reference)
val idxWithData = Indices(0, 0, 0) // All secrets (preimage/digest and key pair) are available
val idxWithoutData = Indices(0, 1, 0) // Secrets are not available, thus some proofs cannot be generated

// Transaction builder will create 1 input 1 output.
// output attestation is trivial
// input attestation is N of 5 (each operation)

val tx3of5NoData = TransactionBuilder.constructTestTransaction(3, idxWithoutData)
runTest(
  "verify idx without data cannot be satisfied for N=3 (valid context)",
  tx3of5NoData,
  false,
  5,
  5
)
runTest(
  "verify idx without data cannot be satisfied for N=3 (invalid context)",
  tx3of5NoData,
  false,
  100,
  100
)



val tx2of5NoData = TransactionBuilder.constructTestTransaction(2, idxWithoutData)
runTest(
  "verify idx without data can be satisfied with N=2 (valid context only)",
  tx2of5NoData,
  true,
  5,
  5
)
runTest(
  "verify idx without data cannot be satisfied with N=2 (invalid context only)",
  tx2of5NoData,
  false,
  100,
  100
)


val tx5of5WithData = TransactionBuilder.constructTestTransaction(5, idxWithData)
runTest(
  "verify idx with data cannot be satisfied for N=5 (because of Locked)",
  tx5of5WithData,
  false,
  5,
  5
)


val tx4of5WithData = TransactionBuilder.constructTestTransaction(4, idxWithData)
runTest(
  "verify idx with data can be satisfied n=4 (only with valid context)",
  tx4of5WithData,
  true,
  5,
  5
)
runTest(
  "verify idx with data cannot be satisfied n=4 (only with invalid context)",
  tx4of5WithData,
  false,
  100,
  100
)


val tx2of5WithData = TransactionBuilder.constructTestTransaction(2, idxWithData)
runTest(
  "verify idx with data can be satisfied n=2 (valid context)",
  tx2of5WithData,
  true,
  5,
  5
)
runTest(
  "verify idx with data can be satisfied n=2 (invalid context)",
  tx2of5WithData,
  true,
  100,
  100
)
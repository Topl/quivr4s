/**
* A worksheet for using the Credentialler
* How the IoTransaction is created is irrelevant
* */
import co.topl.brambl.{Context, Credentials, TransactionBuilder}
import co.topl.brambl.Models._
import co.topl.node.{Events}
import co.topl.node.transaction.{Datums, IoTransaction}
import co.topl.quivr.runtime.{DynamicContext}
import co.topl.node.typeclasses.ContainsSignable.instances.ioTransactionSignable

// A "Valid" context is one where the hardcoded Height and Tick propositions will be satisfied (value of 5).
def getValidContext(tx: IoTransaction): DynamicContext[Option, String] = Context.ToplContext(tx, Some(5),
  Map("header" -> Some(Datums.headerDatum(Events.Header(5)))))

// An "Invalid" context is one where the hardcoded Height and Tick propositions will fail (value of 100).
def getInvalidContext(tx: IoTransaction): DynamicContext[Option, String] = Context.ToplContext(tx, Some(100),
  Map("header" -> Some(Datums.headerDatum(Events.Header(100)))))

// Helpers for manual testing
def checkSignableBytesAreNotMutated(unprovenTx: IoTransaction, provenTx: IoTransaction): Boolean = {
  val unprovenSig = ioTransactionSignable.signableBytes(unprovenTx)
  val provenSig = ioTransactionSignable.signableBytes(provenTx)
  provenSig sameElements unprovenSig
}

def runTest(unprovenTx: IoTransaction, expectedPass: Boolean, createCtx: IoTransaction => DynamicContext[Option, String]): Unit = {
  implicit val ctx: DynamicContext[Option, String] = createCtx(unprovenTx)
  val proven = Credentials.prove(unprovenTx)
  val bytesSame = checkSignableBytesAreNotMutated(unprovenTx, proven)
  val isAuthorized = Credentials.validate(proven)
  val testPassed = isAuthorized == expectedPass && bytesSame
  println(s"Test ${if(testPassed) "passed" else "failed" }")
}


// Both of these indices are registered to have a known identifier in the wallet (i.e, a previous tx reference)
val idxWithData = Indices(0, 0, 0) // All secrets (preimage/digest and key pair) are available
val idxWithoutData = Indices(0, 1, 0) // Secrets are not available, thus some proofs cannot be generated

// All attestations are N of 5. The 5 being Locked, Digest, Signature, Height, and Tick.
// Both Digest and Signature require access to secrets.


// 3 of 5 Attestation. Credentialler has no access to secrets so only Height and Tick can possibly pass
// => Always fail since 3 is required
val tx3of5NoData = TransactionBuilder.constructTestTransaction(3, idxWithoutData)
// Valid context := Height and Tick pass, but Tx invalid since does not meet threshold
runTest(tx3of5NoData, false, getValidContext)
// Invalid context := all proofs fail
runTest(tx3of5NoData,false, getInvalidContext)


// 2 of 5 Attestation. Credentialler has no access to secrets so only Height and Tick can possibly pass
// => Will pass when context is valid. Will fail when context is invalid
val tx2of5NoData = TransactionBuilder.constructTestTransaction(2, idxWithoutData)
// Valid context := Height and Tick pass thus threshold is satisfied
runTest(tx2of5NoData,true, getValidContext)
// Invalid context := all proofs fail
runTest(tx2of5NoData,false, getInvalidContext)


// 5 of 5 Attestation. => Will always fail since Locked is one of the propositions
val tx5of5WithData = TransactionBuilder.constructTestTransaction(5, idxWithData)
runTest(tx5of5WithData,false, getValidContext)


// 4 of 5 Attestation. Credentialler has access to all secrets
// => Will pass when context is valid. Will fail when context is invalid
val tx4of5WithData = TransactionBuilder.constructTestTransaction(4, idxWithData)
// Valid context := All proofs pass
runTest(tx4of5WithData,true, getValidContext)
// Invalid context := Height and Tick fail thus threshold is not met
runTest(tx4of5WithData,false, getInvalidContext)

// 2 of 5 Attestation. Credentialler has access to all secrets
// => Will always pass
val tx2of5WithData = TransactionBuilder.constructTestTransaction(2, idxWithData)
// Valid context := All proofs pass
runTest(tx2of5WithData,true, getValidContext)
// Invalid context := Height and Tick fail, but Digest and Signature pass, thus threshold is met
runTest(tx2of5WithData,true, getInvalidContext)
/**
* A worksheet for using the Credentialler
* How the IoTransaction is created is irrelevant
* */
import co.topl.brambl.{Context, QuivrService}
import co.topl.brambl.wallet.{Credentialler, MockStorage}
import co.topl.node.box.Locks
import co.topl.node.{Address, Events, Identifiers}
import co.topl.node.transaction.{Datums, IoTransaction}
import co.topl.brambl.typeclasses.ContainsSignable.instances.ioTransactionSignable

// A "Valid" context is one where the hardcoded Height and Tick propositions will be satisfied (value of 5).
def getValidContext(tx: IoTransaction): Context = Context(tx, 5,
  Map("header" -> Some(Datums.headerDatum(Events.Header(5)))))

// An "Invalid" context is one where the hardcoded Height and Tick propositions will fail (value of 100).
def getInvalidContext(tx: IoTransaction): Context = Context(tx, 100,
  Map("header" -> Some(Datums.headerDatum(Events.Header(100)))))

// Helpers for manual testing
def checkSignableBytesAreNotMutated(unprovenTx: IoTransaction, provenTx: IoTransaction): Boolean = {
  val unprovenSig = ioTransactionSignable.signableBytes(unprovenTx)
  val provenSig = ioTransactionSignable.signableBytes(provenTx)
  provenSig.sameElements(unprovenSig)
}

def runTest(unprovenTx: IoTransaction, expectedPass: Boolean, createCtx: IoTransaction => Context): Unit = {
  implicit val ctx: Context = createCtx(unprovenTx)
  Credentialler(MockStorage).prove(unprovenTx) match {
    case Left(l) => println(s"Unable to prove tx: ${l}")
    case Right(r) => {
      val bytesSame = checkSignableBytesAreNotMutated(unprovenTx, r)
      val isAuthorized = Credentialler(MockStorage).validate(r).isEmpty
      val testPassed = isAuthorized == expectedPass && bytesSame
      println(s"Test ${if (testPassed) "passed" else "failed"}")
    }
  }
}

val builder = MockTransactionBuilder(MockStorage)

val outputAddress = Address(0, 0, Identifiers.boxLock32(
  Locks.Predicate(List(QuivrService.lockedProposition.get), 1)
))


// All attestations are N of 5. The 5 being Locked, Digest, Signature, Height, and Tick.
// Both Digest and Signature require access to secrets.


// 2 of 5 Attestation. Credentialler has access to all secrets
// => Will always pass
val tx2of5WithData = builder.constructTransaction(MockStorage.addr2a, outputAddress).get
// Valid context := All proofs pass
runTest(tx2of5WithData, expectedPass = true, getValidContext)
// Invalid context := Height and Tick fail, but Digest and Signature pass, thus threshold is met
runTest(tx2of5WithData,expectedPass = true, getInvalidContext)


// 2 of 5 Attestation. Credentialler has no access to secrets so only Height and Tick can possibly pass
// => Will pass when context is valid. Will fail when context is invalid
val tx2of5NoData = builder.constructTransaction(MockStorage.addr2b, outputAddress).get
// Valid context := Height and Tick pass thus threshold is satisfied
runTest(tx2of5NoData, expectedPass = true, getValidContext)
// Invalid context := all proofs fail
runTest(tx2of5NoData, expectedPass = false, getInvalidContext)


// 3 of 5 Attestation. Credentialler has no access to secrets so only Height and Tick can possibly pass
// => Always fail since 3 is required
val tx3of5NoData = builder.constructTransaction(MockStorage.addr3, outputAddress).get
// Valid context := Height and Tick pass, but Tx invalid since does not meet threshold
runTest(tx3of5NoData, expectedPass = false, getValidContext)
// Invalid context := all proofs fail
runTest(tx3of5NoData, expectedPass = false, getInvalidContext)


// 4 of 5 Attestation. Credentialler has access to all secrets
// => Will pass when context is valid. Will fail when context is invalid
val tx4of5WithData = builder.constructTransaction(MockStorage.addr4, outputAddress).get
// Valid context := All proofs pass
runTest(tx4of5WithData, expectedPass = true, getValidContext)
// Invalid context := Height and Tick fail thus threshold is not met
runTest(tx4of5WithData, expectedPass = false, getInvalidContext)


// 5 of 5 Attestation. => Will always fail since Locked is one of the propositions
val tx5of5WithData = builder.constructTransaction(MockStorage.addr5, outputAddress).get
runTest(tx5of5WithData, expectedPass = false, getValidContext)

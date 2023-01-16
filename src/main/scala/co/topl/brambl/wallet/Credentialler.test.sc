/**
* A worksheet for using the Credentialler
* How the IoTransaction is created is irrelevant
* */
import cats.implicits.catsSyntaxOptionId
import co.topl.brambl.Context
import co.topl.brambl.builders.{BuilderError, MockLockBuilder, MockTransactionBuilder}
import co.topl.brambl.models.box.Value
import co.topl.brambl.models.builders.{InputBuildRequest, OutputBuildRequest}
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.wallet.{MockCredentialler, MockStorage}
import co.topl.brambl.typeclasses.ContainsSignable.instances.ioTransactionSignable
import co.topl.brambl.models.{Datum, Event, KnownIdentifier}
import com.google.protobuf.ByteString
import quivr.models.Int128


// A "Valid" context is one where the hardcoded Height and Tick propositions will be satisfied (value of 5).
def getValidContext(tx: IoTransaction): Context = Context(tx, 5,
  Map("header" -> Some(Datum().withHeader(Datum.Header(Event.Header(5).some)))))

// An "Invalid" context is one where the hardcoded Height and Tick propositions will fail (value of 100).
def getInvalidContext(tx: IoTransaction): Context = Context(tx, 100,
  Map("header" -> Some(Datum().withHeader(Datum.Header(Event.Header(100).some)))))

// Helpers for manual testing
def checkSignableBytesAreNotMutated(unprovenTx: IoTransaction, provenTx: IoTransaction): Boolean = {
  val unprovenSig = ioTransactionSignable.signableBytes(unprovenTx)
  val provenSig = ioTransactionSignable.signableBytes(provenTx)
  provenSig.toByteArray.sameElements(unprovenSig.toByteArray)
}

def runTest(builtTx: Either[List[BuilderError], IoTransaction], expectedPass: Boolean, createCtx: IoTransaction => Context): Unit = {
  builtTx match {
    case Left(errors) => println(s"Failed to build transaction: $errors")
    case Right(unprovenTx) =>
      MockCredentialler.prove(unprovenTx) match {
        case Left(l) => println(s"Unable to prove tx: ${l}")
        case Right(r) => {
          val bytesSame = checkSignableBytesAreNotMutated(unprovenTx, r)
          val isAuthorized = MockCredentialler.validate(r, createCtx(unprovenTx)).isEmpty
          val testPassed = isAuthorized == expectedPass && bytesSame
          println(s"Test ${if (testPassed) "passed" else "failed"}")
        }
      }
  }
}

val outputRequest = OutputBuildRequest(
  MockLockBuilder.constructMockLockTrivial.some,
  Value().withToken(Value.Token(Int128(ByteString.copyFrom(BigInt(1).toByteArray)).some)).some
)


// All attestations are N of 5. The 5 being Locked, Digest, Signature, Height, and Tick.
// Both Digest and Signature require access to secrets.


// 2 of 5 Attestation. Credentialler has access to all secrets
// => Will always pass
val tx2of5WithData = MockTransactionBuilder.constructUnprovenTransaction(
  List(InputBuildRequest(KnownIdentifier().withTransactionOutput32(MockStorage.dummyTxIdentifier2a).some)),
  List(outputRequest)
)
// Valid context := All proofs pass
runTest(tx2of5WithData, expectedPass = true, getValidContext)
// Invalid context := Height and Tick fail, but Digest and Signature pass, thus threshold is met
runTest(tx2of5WithData,expectedPass = true, getInvalidContext)


// 2 of 5 Attestation. Credentialler has no access to secrets so only Height and Tick can possibly pass
// => Will pass when context is valid. Will fail when context is invalid
val tx2of5NoData = MockTransactionBuilder.constructUnprovenTransaction(
  List(InputBuildRequest(KnownIdentifier().withTransactionOutput32(MockStorage.dummyTxIdentifier2b).some)),
  List(outputRequest)
)
// Valid context := Height and Tick pass thus threshold is satisfied
runTest(tx2of5NoData, expectedPass = true, getValidContext)
// Invalid context := all proofs fail
runTest(tx2of5NoData, expectedPass = false, getInvalidContext)


// 3 of 5 Attestation. Credentialler has no access to secrets so only Height and Tick can possibly pass
// => Always fail since 3 is required
val tx3of5NoData = MockTransactionBuilder.constructUnprovenTransaction(
  List(InputBuildRequest(KnownIdentifier().withTransactionOutput32(MockStorage.dummyTxIdentifier3).some)),
  List(outputRequest)
)
// Valid context := Height and Tick pass, but Tx invalid since does not meet threshold
runTest(tx3of5NoData, expectedPass = false, getValidContext)
// Invalid context := all proofs fail
runTest(tx3of5NoData, expectedPass = false, getInvalidContext)


// 4 of 5 Attestation. Credentialler has access to all secrets
// => Will pass when context is valid. Will fail when context is invalid
val tx4of5WithData = MockTransactionBuilder.constructUnprovenTransaction(
  List(InputBuildRequest(KnownIdentifier().withTransactionOutput32(MockStorage.dummyTxIdentifier4).some)),
  List(outputRequest)
)
// Valid context := All proofs pass
runTest(tx4of5WithData, expectedPass = true, getValidContext)
// Invalid context := Height and Tick fail thus threshold is not met
runTest(tx4of5WithData, expectedPass = false, getInvalidContext)


// 5 of 5 Attestation. => Will always fail since Locked is one of the propositions
val tx5of5WithData = MockTransactionBuilder.constructUnprovenTransaction(
  List(InputBuildRequest(KnownIdentifier().withTransactionOutput32(MockStorage.dummyTxIdentifier5).some)),
  List(outputRequest)
)
runTest(tx5of5WithData, expectedPass = false, getValidContext)

/**
* A worksheet for using the Transaction Builder
* */
import cats.implicits.catsSyntaxOptionId
import co.topl.brambl.Context
import co.topl.brambl.builders.{BuilderError, MockLockBuilder, MockTransactionBuilder}
import co.topl.brambl.models.box.Value
import co.topl.brambl.models.builders.{InputBuildRequest, OutputBuildRequest}
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.models.{Datum, Event, KnownIdentifier}
import co.topl.brambl.transaction.validators.ValidationError
import co.topl.brambl.wallet.{MockCredentialler, MockStorage}
import com.google.protobuf.ByteString
import quivr.models.Int128

// Output Request
val outputRequest = OutputBuildRequest(
  // Output lock is used to create the Address. Using a trivial Locked lock
  MockLockBuilder.constructMockLock1of1Locked.some,
  // Output value. Trivial 1
  Value().withToken(Value.Token(Int128(ByteString.copyFrom(BigInt(1).toByteArray)).some)).some
)

// Input Request
val inputRequest = InputBuildRequest(
  // KnownIdentifier points to an existing Transaction Output.
  // This existing txOutput already has a lock and value associated with it
  KnownIdentifier().withTransactionOutput32(MockStorage.dummyTxIdentifier2a).some
)

// Build Transaction using the requests
// Will be list of build errors or the built transaction
val transaction: Either[List[BuilderError], IoTransaction] = MockTransactionBuilder.constructUnprovenTransaction(
  List(inputRequest),
  List(outputRequest),
)

transaction match {
  case Left(errors) => println(s"Transaction build failed with errors: $errors")
  case Right(tx) => {
    println(s"Transaction build succeeded: $tx")

    // Syntax validation. Commented out since it is not implemented yet
    // TransactionSyntaxInterpreter.make[Id]().validate(tx)

    // Prove and validate (Transaction Authorization) the transaction
    val ctx = Context(tx, 5, Map("header" -> Some(Datum().withHeader(Datum.Header(Event.Header(5).some)))))
    val proven: Either[List[ValidationError], IoTransaction] = MockCredentialler.proveAndValidate(tx, ctx)

    proven match {
      case Left(errors) => println(s"Transaction proveAndValidation failed with errors: $errors")
      case Right(tx) => println(s"Transaction validation succeeded: $tx")
    }

    // Afterwards, the transaction can be broadcasted to the network
  }
}


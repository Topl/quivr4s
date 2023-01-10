package co.topl.brambl.transaction.validators.syntax

import cats.Monad
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.models.Datum
import co.topl.quivr.api.Verifier

/**
 * Validates that a Transaction is syntactically correct.
 */
object TransactionSyntaxInterpreter {

  def make[F[_]: Monad]()(implicit verifier: Verifier[F, Datum]): TransactionSyntaxVerifier[F] =
    new TransactionSyntaxVerifier[F] {

      /**
       * Verifies the syntax of the Transaction
       */
      override def validate(transaction: IoTransaction
      ): F[Either[TransactionSyntaxError, IoTransaction]] = ???
    }
}

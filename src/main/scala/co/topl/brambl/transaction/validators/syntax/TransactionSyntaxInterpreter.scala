package co.topl.brambl.transaction.validators.syntax

import cats.Monad
import co.topl.brambl.models.transaction.IoTransaction

/**
 * Validates that a Transaction is syntactically correct.
 */
object TransactionSyntaxInterpreter {

  def make[F[_]: Monad](): TransactionSyntaxVerifier[F] =
    new TransactionSyntaxVerifier[F] {

      /**
       * Verifies the syntax of the Transaction
       */
      override def validate(transaction: IoTransaction
      ): F[Either[TransactionSyntaxError, IoTransaction]] = ???
    }
}

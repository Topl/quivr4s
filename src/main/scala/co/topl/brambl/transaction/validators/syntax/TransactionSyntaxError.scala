package co.topl.brambl.transaction.validators.syntax

import co.topl.brambl.models.transaction.Attestation
import co.topl.brambl.transaction.validators.ValidationError

sealed abstract class TransactionSyntaxError extends ValidationError

object TransactionSyntaxErrors {
  /**
   * Error for when a transaction is not syntactically valid because its attestation is formed incorrectly
   */
  case class AttestationMalformed(attestation: Attestation) extends TransactionSyntaxError
}

package co.topl.brambl.wallet

import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.transaction.validators.ValidationError
import co.topl.brambl.transaction.validators.authorization.TransactionAuthorizationError
import co.topl.brambl.transaction.validators.syntax.TransactionSyntaxError

trait Credentials {
  def prove(unprovenTx:            IoTransaction): Either[List[TransactionSyntaxError], IoTransaction]
  def validate(tx:                 IoTransaction): List[TransactionAuthorizationError]
  def proveAndValidate(unprovenTx: IoTransaction): Either[List[ValidationError], IoTransaction]
}

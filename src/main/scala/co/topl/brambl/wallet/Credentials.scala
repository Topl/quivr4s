package co.topl.brambl.wallet

import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.wallet.CredentiallerErrors.ProverError
import co.topl.brambl.wallet.CredentiallerErrors.ValidationError

trait Credentials {
  def prove(unprovenTx:            IoTransaction): Either[List[ProverError], IoTransaction]
  def validate(tx:                 IoTransaction): List[ValidationError]
  def proveAndValidate(unprovenTx: IoTransaction): Either[List[CredentiallerError], IoTransaction]
}

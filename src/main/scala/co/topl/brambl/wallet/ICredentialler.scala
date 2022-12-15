package co.topl.brambl.wallet

import co.topl.brambl.wallet.CredentiallerErrors.{ProverError, ValidationError}
import co.topl.node.transaction.IoTransaction

trait ICredentialler {
  def prove(unprovenTx: IoTransaction): Either[List[ProverError], IoTransaction]
  def validate(tx: IoTransaction): List[ValidationError]
  def proveAndValidate(unprovenTx: IoTransaction): Either[List[CredentiallerError], IoTransaction]
}




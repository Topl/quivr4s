package co.topl.brambl.wallet

import co.topl.node.transaction.IoTransaction
import co.topl.node.transaction.authorization.ValidationError

trait ICredentialler {
  def prove(unprovenTx: IoTransaction): Either[List[CredentiallerError], IoTransaction]
  def validate(tx: IoTransaction): Boolean
  def proveAndValidate(unprovenTx: IoTransaction): Either[ValidationError, IoTransaction]
}




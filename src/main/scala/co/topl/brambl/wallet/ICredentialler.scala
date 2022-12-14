package co.topl.brambl.wallet

import co.topl.node.transaction.IoTransaction
import co.topl.node.transaction.authorization.ValidationError
import co.topl.quivr.runtime.DynamicContext

trait ICredentialler {
  def prove(unprovenTx: IoTransaction): Either[List[CredentiallerError], IoTransaction]
  def validate(tx: IoTransaction)(implicit ctx: DynamicContext[Option, String]): Boolean
  def proveAndValidate(unprovenTx: IoTransaction)(implicit ctx: DynamicContext[Option, String]): Either[ValidationError, IoTransaction]
}




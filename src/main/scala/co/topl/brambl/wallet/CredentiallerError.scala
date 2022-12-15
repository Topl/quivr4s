package co.topl.brambl.wallet

import co.topl.node.KnownIdentifier
import co.topl.node.transaction.authorization

sealed abstract class CredentiallerError

object CredentiallerErrors {

  abstract class ProverError extends CredentiallerError
  case class KnownIdentifierUnknown(knownIdentifier: KnownIdentifier) extends ProverError
  case class ValidationError(error: authorization.ValidationError) extends CredentiallerError

}

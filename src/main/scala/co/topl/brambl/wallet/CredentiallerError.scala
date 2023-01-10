package co.topl.brambl.wallet

import co.topl.brambl.models.transaction.Attestation
import co.topl.brambl.authorization.{ValidationError => AuthorizationValidationError}

sealed abstract class CredentiallerError

object CredentiallerErrors {

  abstract class ProverError extends CredentiallerError
  case class AttestationMalformed(attestation: Attestation) extends ProverError
  case class ValidationError(error: AuthorizationValidationError) extends CredentiallerError

}

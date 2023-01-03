package co.topl.brambl.wallet

import co.topl.node.transaction.{Attestation, authorization}

sealed abstract class CredentiallerError

object CredentiallerErrors {

  abstract class ProverError extends CredentiallerError
  case class AttestationMalformed(attestation: Attestation) extends ProverError
  case class ValidationError(error: authorization.ValidationError) extends CredentiallerError

}

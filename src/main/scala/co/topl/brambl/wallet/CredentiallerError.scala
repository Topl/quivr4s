package co.topl.brambl.wallet

import co.topl.node.KnownIdentifier

sealed abstract class CredentiallerError

object CredentiallerErrors {
  case class KnownIdentifierUnknown(knownIdentifier: KnownIdentifier) extends CredentiallerError
}

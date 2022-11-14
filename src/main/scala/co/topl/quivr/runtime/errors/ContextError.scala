package co.topl.quivr.runtime.errors

import co.topl.quivr

sealed abstract class ContextError extends quivr.runtime.Error

object ContextErrors {
  /**
   *
   * @param message field for returning a message upstream to indicate the erroneous label
   */
  case object FailedToFindDigestVerifier extends ContextError
  case object FailedToFindSignatureVerifier extends ContextError
  case object FailedToFindDatum extends ContextError
}

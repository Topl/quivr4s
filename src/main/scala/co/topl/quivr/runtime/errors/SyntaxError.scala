package co.topl.quivr.runtime.errors

import co.topl.quivr
import co.topl.quivr.Proof

sealed abstract class SyntaxError extends quivr.runtime.Error

object SyntaxErrors {
  /**
   * A Syntax error indicating that the proof included in the transaction was invalid
   */
  case class MessageAuthorizationFailed(proof: Proof) extends SyntaxError
}



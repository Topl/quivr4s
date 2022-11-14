package co.topl.quivr.runtime.errors

import co.topl.quivr
import co.topl.quivr.{Proof, Proposition}

sealed abstract class AuthorizationError extends quivr.runtime.Error

object AuthorizationErrors {

  /**
   * An Authorization error indicating that this transaction was invalid only within the provided validation context.
   * It _might_ become valid later (or perhaps it _was_ valid previously)
   * (i.e. height lock)
   */
  case class EvaluationAuthorizationFailed(proposition: Proposition, proof: Proof) extends AuthorizationError
}

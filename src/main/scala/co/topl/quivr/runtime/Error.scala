package co.topl.quivr.runtime

    import co.topl.quivr
import co.topl.quivr.{Proof, Proposition}

trait Error

object Errors {
sealed abstract class AuthorizationError extends quivr.runtime.Error
sealed abstract class ContextError extends quivr.runtime.Error
sealed abstract class SyntaxError extends quivr.runtime.Error

object AuthorizationErrors {

  /**
   * An Authorization error indicating that this transaction was invalid only within the provided validation context.
   * It _might_ become valid later (or perhaps it _was_ valid previously)
   * (i.e. height lock)
   */
  case class EvaluationAuthorizationFailed(proposition: Proposition, proof: Proof) extends AuthorizationError
  case object LockedPropositionIsUnsatisfiable extends AuthorizationError
}

object ContextErrors {
  /**
   *
   * @param message field for returning a message upstream to indicate the erroneous label
   */
  case object FailedToFindDigestVerifier extends ContextError
  case object FailedToFindSignatureVerifier extends ContextError
  case object FailedToFindDatum extends ContextError
}

object SyntaxErrors {
  /**
   * A Syntax error indicating that the proof included in the transaction was invalid
   */
  case class MessageAuthorizationFailed(proof: Proof) extends SyntaxError
}

}

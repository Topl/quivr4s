package co.topl.quivr.runtime

import co.topl.quivr.{Proof, Proposition}

sealed abstract class QuivrRuntimeError

object QuivrRuntimeErrors {

  /**
   * A Validation error indicates that the evaluation of the proof failed for the given proposition within the provided context.
   */
  object ValidationError {
    case class EvaluationAuthorizationFailed(proposition: Proposition, proof: Proof) extends QuivrRuntimeError
    case class MessageAuthorizationFailed(proof: Proof) extends QuivrRuntimeError
    case object LockedPropositionIsUnsatisfiable extends QuivrRuntimeError
  }

  /**
   * A Context error indicates that the Dynamic context failed to retrieve an instance of a requested member
   */
  object ContextError {
    case object FailedToFindDigestVerifier extends QuivrRuntimeError
    case object FailedToFindSignatureVerifier extends QuivrRuntimeError
    case object FailedToFindDatum extends QuivrRuntimeError
    case object FailedToFindInterface extends QuivrRuntimeError
  }
}

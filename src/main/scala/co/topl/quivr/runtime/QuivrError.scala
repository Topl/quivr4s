package co.topl.quivr.runtime

import co.topl.quivr.{Proof, Proposition}

sealed abstract class QuivrError

object Errors {

  object AuthorizationErrors {

    /**
     * An Authorization error indicating that this transaction was invalid only within the provided validation context.
     * It _might_ become valid later (or perhaps it _was_ valid previously)
     * (i.e. height lock)
     */
    case class EvaluationAuthorizationFailed(proposition: Proposition, proof: Proof) extends QuivrError
    case object LockedPropositionIsUnsatisfiable extends QuivrError
  }

  object ContextErrors {

    /**
     * @param message field for returning a message upstream to indicate the erroneous label
     */
    case object FailedToFindDigestVerifier extends QuivrError
    case object FailedToFindSignatureVerifier extends QuivrError
    case object FailedToFindDatum extends QuivrError
    case object FailedToFindInterface extends QuivrError
  }

  object SyntaxErrors {

    /**
     * A Syntax error indicating that the proof included in the transaction was invalid
     */
    case class MessageAuthorizationFailed(proof: Proof) extends QuivrError
  }

}

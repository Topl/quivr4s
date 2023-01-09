package co.topl.node.transaction.authorization

import quivr.models.{Proof, Proposition}

sealed abstract class ValidationError

object ValidationErrors {
  case object ValidationFailed extends ValidationError

  /**
   * An Authorization error indicating that this transaction was invalid only within the provided validation context.
   * It _might_ become valid later (or perhaps it _was_ valid previously)
   * (i.e. height lock)
   */
  case class Contextual(proposition: Proposition, proof: Proof) extends ValidationError

  /**
   * An Authorization error indicating that this transaction will never be valid.  This is usually the result of something
   * that could _probably_ be determined in a syntactic validation check, meaning no context is needed to perform the
   * authorization validation.  (i.e. invalid signature)
   */
  case class Permanent(proposition: Proposition, proof: Proof) extends ValidationError
}

package co.topl.quivr

import cats.Applicative
import cats.implicits._
import co.topl.Crypto
import co.topl.quivr.Evaluation.IncludesHeight

// Proposers create Propositions from a tuple of arguments (or single argument) of type A.
trait Proposer[F[_], A] {
  def propose(args: A): F[Proposition]
}

// This represents the native apply (constructor) methods for the returned Proposition
// todo: we probably need to introduce another layer of type guarantees or validation for generic Array[Byte] types
object Proposer {

  def LockedProposer[F[_]: Applicative, A]: Proposer[F, Option[Array[Byte]]] =
    (data: Option[Array[Byte]]) => Models.Primitive.Locked.Proposition(data).pure[F].widen

  def digestProposer[F[_]: Applicative, A]: Proposer[F, Array[Byte]] =
    (digest: Array[Byte]) => Models.Primitive.Digest.Proposition(digest).pure[F].widen

  def signatureProposer[F[_]: Applicative, A]: Proposer[F, (String, Crypto.VerificationKey)] =
    (args: (String, Crypto.VerificationKey)) => Models.Primitive.DigitalSignature.Proposition(args._1, args._2).pure[F].widen

  def heightProposer[F[_]: Applicative, A]: Proposer[F, (String, Long, Long)] =
    (args: (String, Long, Long)) => Models.Contextual.HeightRange.Proposition(args._1, args._2, args._3).pure[F].widen

  def tickProposer[F[_]: Applicative, A]: Proposer[F, (Long, Long)] =
    (args: (Long, Long)) => Models.Contextual.TickRange.Proposition(args._1, args._2).pure[F].widen

  // def exactMatchProposer

  def thresholdProposer[F[_]: Applicative, A]: Proposer[
    F,
    (Array[Proposition], Int, Models.Compositional.Threshold.BooleanOp)
  ] =
    (args: (Array[Proposition], Int, Models.Compositional.Threshold.BooleanOp)) =>
      Models.Compositional.Threshold
        .Proposition(args._1, args._2, args._3)
        .pure[F]
        .widen

  def notProposer[F[_]: Applicative]: Proposer[F, Proposition] =
    (proposition: Proposition) =>
      Models.Compositional.Not
        .Proposition(proposition)
        .pure[F]
        .widen
}

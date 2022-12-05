package co.topl.quivr.api

import cats.Applicative
import cats.implicits._
import co.topl.common.Data
import co.topl.common.Models.{Digest, VerificationKey}
import co.topl.quivr.{Models, Proposition}

// Proposers create Propositions from a tuple of arguments (or single argument) of type A.
trait Proposer[F[_], A, P <: Proposition] {
  def propose(args: A): F[P]
}

// This represents the native apply (constructor) methods for the returned Proposition
object Proposer {

  def LockedProposer[F[_]: Applicative, A]: Proposer[F, Option[Data], Models.Primitive.Locked.Proposition] =
    (data: Option[Data]) => Models.Primitive.Locked.Proposition(data).pure[F].widen

  def digestProposer[F[_]: Applicative, A]: Proposer[F, (String, Digest), Models.Primitive.Digest.Proposition] =
    (args: (String, Digest)) => Models.Primitive.Digest.Proposition(args._1, args._2).pure[F].widen

  def signatureProposer[F[_]: Applicative, A]
    : Proposer[F, (String, VerificationKey), Models.Primitive.DigitalSignature.Proposition] =
    (args: (String, VerificationKey)) => Models.Primitive.DigitalSignature.Proposition(args._1, args._2).pure[F].widen

  def heightProposer[F[_]: Applicative, A]
    : Proposer[F, (String, Long, Long), Models.Contextual.HeightRange.Proposition] =
    (args: (String, Long, Long)) => Models.Contextual.HeightRange.Proposition(args._1, args._2, args._3).pure[F].widen

  def tickProposer[F[_]: Applicative, A]: Proposer[F, (Long, Long), Models.Contextual.TickRange.Proposition] =
    (args: (Long, Long)) => Models.Contextual.TickRange.Proposition(args._1, args._2).pure[F].widen

  def exactMatchProposer[F[_]: Applicative, A]
    : Proposer[F, (String, Array[Byte]), Models.Contextual.ExactMatch.Proposition] =
    (args: (String, Array[Byte])) => Models.Contextual.ExactMatch.Proposition(args._1, args._2).pure[F].widen

  def lessThanProposer[F[_]: Applicative, A]: Proposer[F, (String, Long), Models.Contextual.LessThan.Proposition] =
    (args: (String, Long)) => Models.Contextual.LessThan.Proposition(args._1, args._2).pure[F].widen

  def greaterThan[F[_]: Applicative, A]: Proposer[F, (String, Long), Models.Contextual.GreaterThan.Proposition] =
    (args: (String, Long)) => Models.Contextual.GreaterThan.Proposition(args._1, args._2).pure[F].widen

  def equalTo[F[_]: Applicative, A]: Proposer[F, (String, Long), Models.Contextual.EqualTo.Proposition] =
    (args: (String, Long)) => Models.Contextual.EqualTo.Proposition(args._1, args._2).pure[F].widen

  def thresholdProposer[F[_]: Applicative, A]
    : Proposer[F, (Set[Proposition], Int), Models.Compositional.Threshold.Proposition] =
    (args: (Set[Proposition], Int)) =>
      Models.Compositional.Threshold
        .Proposition(args._1, args._2)
        .pure[F]
        .widen

  def notProposer[F[_]: Applicative]: Proposer[F, Proposition, Models.Compositional.Not.Proposition] =
    (proposition: Proposition) =>
      Models.Compositional.Not
        .Proposition(proposition)
        .pure[F]
        .widen

  def andProposer[F[_]: Applicative]: Proposer[F, (Proposition, Proposition), Models.Compositional.And.Proposition] =
    (args: (Proposition, Proposition)) =>
      Models.Compositional.And
        .Proposition(args._1, args._2)
        .pure[F]
        .widen

  def orProposer[F[_]: Applicative]: Proposer[F, (Proposition, Proposition), Models.Compositional.Or.Proposition] =
    (args: (Proposition, Proposition)) =>
      Models.Compositional.Or
        .Proposition(args._1, args._2)
        .pure[F]
        .widen
}

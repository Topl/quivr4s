package co.topl.quivr

import cats.Applicative
import cats.implicits._
import co.topl.crypto.hash.blake2b256

// Proposers create Propositions from a tuple of arguments (or single argument) of type A.
trait Proposer[F[_], A] {
  def propose(args: A): F[Proposition]
}

// This represents the native apply (constructor) methods for the returned Proposition
// todo: we probably need to introduce another layer of type guarantees or validation for generic Array[Byte] types
object Proposer {

  def LockedProposer[F[_] : Applicative, A]: Proposer[F, Array[Byte]] =
    new Proposer[F, Array[Byte]] {
      def propose(data: Array[Byte]): F[Proposition] =
        Models.Primitive.Locked.Proposition(data).pure[F].widen
    }

  def digestProposer[F[_] : Applicative, A]: Proposer[F, Array[Byte]] =
    new Proposer[F, Array[Byte]] {
      def propose(digest: Array[Byte]): F[Proposition] =
        Models.Primitive.Digest.Proposition(digest).pure[F].widen
    }

  def signatureProposer[F[_] : Applicative, A]: Proposer[F, Array[Byte]] =
    new Proposer[F, Array[Byte]] {
      def propose(vk: Array[Byte]): F[Proposition] =
        Models.Primitive.DigitalSignature.Proposition(vk).pure[F].widen
    }

  def heightProposer[F[_] : Applicative, A]: Proposer[F, (Long, Long)] =
    new Proposer[F, (Long, Long)] {
      def propose(args: (Long, Long)): F[Proposition] =
        Models.Contextual.Height.Proposition(args._1, args._2).pure[F].widen
    }

  def slotProposer[F[_] : Applicative, A]: Proposer[F, (Long, Long)] =
    new Proposer[F, (Long, Long)] {
      def propose(args: (Long, Long)): F[Proposition] =
        Models.Contextual.Slot.Proposition(args._1, args._2).pure[F].widen
    }

  // def exactMatchProposer

  def thresholdProposer[F[_] : Applicative, A]: Proposer[
    F,
    (Array[Proposition], Int, Models.Compositional.Threshold.BooleanOp)
  ] =
    new Proposer[F, (Array[Proposition], Int, Models.Compositional.Threshold.BooleanOp)] {
      def propose(args: (Array[Proposition], Int, Models.Compositional.Threshold.BooleanOp)): F[Proposition] =
        Models.Compositional.Threshold
          .Proposition(args._1, args._2, args._3)
          .pure[F]
          .widen
    }

  def notProposer[F[_] : Applicative]: Proposer[F, Proposition] =
    new Proposer[F, Proposition] {
      def propose(proposition: Proposition): F[Proposition] =
        Models.Compositional.Not
          .Proposition(proposition)
          .pure[F]
          .widen
    }
}

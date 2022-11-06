package co.topl.quivr

import cats.Applicative
import cats.implicits._
import co.topl.crypto.hash.blake2b256

// Provers create proofs that are bound to the transactions which execute the proof.
//
// This provides a generic way to map all computations (single-step or sigma-protocol)
// into a Fiat-Shamir hueristic if the bind that is used here is unique.
// This seems like it would promote statelessness but I am unsure how.
trait Prover[F[_], A] {
  def prove(args: A)(message: Array[Byte]): F[Proof]
}

object Prover {

  /**
   * @param tag
   * an identifier of the Operation
   * @param message
   * unique bytes from a transaction that will be bound to the proof
   * @return
   * an array of bytes that is similar to a "signature" for the proof
   */
  def bind(tag: Byte, message: Array[Byte]): Array[Byte] =
    blake2b256.hash(message :+ tag).value

  def lockedProver[F[_] : Applicative]: Prover[F, Unit] =
    new Prover[F, Unit] {
      def prove(args: Unit)(message: Array[Byte]): F[Proof] =
        Models.Primitive.Locked
          .Proof(
            bind(Models.Primitive.Locked.token, message)
          )
          .pure[F]
          .widen
    }

  def digestProver[F[_] : Applicative]: Prover[F, Array[Byte]] =
    new Prover[F, Array[Byte]] {

      def prove(preimage: Array[Byte])(message: Array[Byte]): F[Proof] =
        Models.Primitive.Digest
          .Proof(
            preimage,
            bind(Models.Primitive.Digest.token, message)
          )
          .pure[F]
          .widen
    }

  def signatureProver[F[_] : Applicative]: Prover[F, Array[Byte]] =
    new Prover[F, Array[Byte]] {

      def prove(sk: Array[Byte])(message: Array[Byte]): F[Proof] =
        Models.Primitive.DigitalSignature
          .Proof(
            bind(Models.Primitive.DigitalSignature.token, sk ++ message)
          )
          .pure[F]
          .widen
    }

  def heightProver[F[_] : Applicative]: Prover[F, Unit] =
    new Prover[F, Unit] {
      def prove(args: Unit)(message: Array[Byte]): F[Proof] =
        Models.Contextual.Height
          .Proof(
            bind(Models.Contextual.Height.token, message)
          )
          .pure[F]
          .widen
    }

  def slotProver[F[_] : Applicative]: Prover[F, Unit] =
    new Prover[F, Unit] {
      def prove(args: Unit)(message: Array[Byte]): F[Proof] =
        Models.Contextual.Height
          .Proof(
            bind(Models.Contextual.Height.token, message)
          )
          .pure[F]
          .widen
    }

  // def exactMatchProver[F[_]: Applicative]: Prover[F, Unit] =
  //   new Prover[F, Unit] {}

  def thresholdProver[F[_] : Applicative]: Prover[F, Array[Option[Proof]]] =
    new Prover[F, Array[Option[Proof]]] {
      def prove(challenges: Array[Option[Proof]])(message: Array[Byte]): F[Proof] =
        Models.Compositional.Threshold
          .Proof(
            challenges,
            bind(Models.Compositional.Threshold.token, message)
          )
          .pure[F]
          .widen
    }

  def notProver[F[_] : Applicative]: Prover[F, Unit] =
    new Prover[F, Unit] {
      def prove(args: Unit)(message: Array[Byte]): F[Proof] =
        Models.Compositional.Not
          .Proof(
            bind(Models.Compositional.Not.token, message)
          )
          .pure[F]
          .widen
    }
}

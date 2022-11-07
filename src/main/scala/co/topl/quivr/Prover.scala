package co.topl.quivr

import cats.Applicative
import cats.implicits._
import co.topl.Crypto
import co.topl.crypto.hash.blake2b256

// Provers create proofs that are bound to the transactions which execute the proof.
//
// This provides a generic way to map all computations (single-step or sigma-protocol)
// into a Fiat-Shamir hueristic if the bind that is used here is unique.
// This seems like it would promote statelessness but I am unsure how.
trait Prover[F[_], A] {
  def prove(args: A, message: SignableTxBytes): F[Proof]
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
  def bind(tag: Byte, message: SignableTxBytes): TxBind =
    blake2b256.hash(message :+ tag).value

  def lockedProver[F[_] : Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableTxBytes) => Models.Primitive.Locked
      .Proof(
        bind(Models.Primitive.Locked.token, message)
      )
      .pure[F]
      .widen

  def digestProver[F[_] : Applicative]: Prover[F, Array[Byte]] =
    (preimage: Array[Byte], message: SignableTxBytes) => Models.Primitive.Digest
      .Proof(
        preimage,
        bind(Models.Primitive.Digest.token, message)
      )
      .pure[F]
      .widen

  def signatureProver[F[_] : Applicative]: Prover[F, Crypto.Witness] =
    (witness: Crypto.Witness, message: SignableTxBytes) => Models.Primitive.DigitalSignature
      .Proof(
        witness,
        bind(Models.Primitive.DigitalSignature.token, message)
      )
      .pure[F]
      .widen

  def heightProver[F[_] : Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableTxBytes) => Models.Contextual.HeightRange
      .Proof(
        bind(Models.Contextual.HeightRange.token, message)
      )
      .pure[F]
      .widen

  def tickProver[F[_] : Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableTxBytes) => Models.Contextual.HeightRange
      .Proof(
        bind(Models.Contextual.HeightRange.token, message)
      )
      .pure[F]
      .widen

  def exactMatchProver[F[_] : Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableTxBytes) => Models.Contextual.ExactMatch
      .Proof(
        bind(Models.Contextual.ExactMatch.token, message)
      )
      .pure[F]
      .widen

  def lessThanProver[F[_] : Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableTxBytes) => Models.Contextual.LessThan
      .Proof(
        bind(Models.Contextual.LessThan.token, message)
      )
      .pure[F]
      .widen

  def greaterThanProver[F[_] : Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableTxBytes) => Models.Contextual.GreaterThan
      .Proof(
        bind(Models.Contextual.GreaterThan.token, message)
      )
      .pure[F]
      .widen

  def equalTo[F[_] : Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableTxBytes) => Models.Contextual.EqualTo
      .Proof(
        bind(Models.Contextual.EqualTo.token, message)
      )
      .pure[F]
      .widen

  def thresholdProver[F[_] : Applicative]: Prover[F, Array[Option[Proof]]] =
    (challenges: Array[Option[Proof]], message: SignableTxBytes) => Models.Compositional.Threshold
      .Proof(
        challenges,
        bind(Models.Compositional.Threshold.token, message)
      )
      .pure[F]
      .widen

  def notProver[F[_] : Applicative]: Prover[F, Proof] =
    (proof: Proof, message: SignableTxBytes) => Models.Compositional.Not
      .Proof(
        proof,
        bind(Models.Compositional.Not.token, message)
      )
      .pure[F]
      .widen

  def andProver[F[_] : Applicative]: Prover[F, (Proof, Proof)] =
    (args: (Proof, Proof), message: SignableTxBytes) => Models.Compositional.And
      .Proof(
        args._1,
        args._2,
        bind(Models.Compositional.And.token, message)
      )
      .pure[F]
      .widen

  def orProver[F[_] : Applicative]: Prover[F, (Proof, Proof)] =
    (args: (Proof, Proof), message: SignableTxBytes) => Models.Compositional.Or
      .Proof(
        args._1,
        args._2,
        bind(Models.Compositional.Or.token, message)
      )
      .pure[F]
      .widen
}

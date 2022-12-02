package co.topl.quivr.api

import cats.Applicative
import cats.implicits._
import co.topl.common.Models.{Preimage, Witness}
import co.topl.crypto.hash.blake2b256
import co.topl.quivr.{Models, Proof, SignableBytes, TxBind}

import java.nio.charset.StandardCharsets

// Provers create proofs that are bound to the transaction which executes the proof.
//
// This provides a generic way to map all computations (single-step or sigma-protocol)
// into a Fiat-Shamir heuristic if the bind that is used here is unique.
// This seems like it would promote statelessness but I am unsure how.
trait Prover[F[_], A, R <: Proof] {

  /**
   * @param args A is product type (tuple) of the inputs needed to satisfy a certain Proposition
   * @param message The unique bytes of the message that the instance of the Proof will be bound to
   * @return a Quivr proof that may be paired with a revealed Proposition in a Verification runtime
   */
  def prove(args: A, message: SignableBytes): F[R]
}

object Prover {

  /**
   * @param tag     an identifier of the Operation
   * @param message unique bytes from a transaction that will be bound to the proof
   * @return an array of bytes that is similar to a "signature" for the proof
   */
  private def blake2b256Bind(tag: String, message: SignableBytes): TxBind =
    blake2b256.hash(tag.getBytes(StandardCharsets.UTF_8) ++ message).value

  def lockedProver[F[_]: Applicative]: Prover[F, Unit, Models.Primitive.Locked.Proof] =
    (_: Unit, _: SignableBytes) => Models.Primitive.Locked.Proof().pure[F]

  def digestProver[F[_]: Applicative]: Prover[F, Preimage, Models.Primitive.Digest.Proof] =
    (preimage: Preimage, message: SignableBytes) =>
      Models.Primitive.Digest
        .Proof(
          preimage,
          blake2b256Bind(Models.Primitive.Digest.token, message)
        )
        .pure[F]

  def signatureProver[F[_]: Applicative]: Prover[F, Witness, Models.Primitive.DigitalSignature.Proof] =
    (witness: Witness, message: SignableBytes) =>
      Models.Primitive.DigitalSignature
        .Proof(
          witness,
          blake2b256Bind(Models.Primitive.DigitalSignature.token, message)
        )
        .pure[F]

  def heightProver[F[_]: Applicative]: Prover[F, Unit, Models.Contextual.HeightRange.Proof] =
    (args: Unit, message: SignableBytes) =>
      Models.Contextual.HeightRange
        .Proof(
          blake2b256Bind(Models.Contextual.HeightRange.token, message)
        )
        .pure[F]

  def tickProver[F[_]: Applicative]: Prover[F, Unit, Models.Contextual.TickRange.Proof] =
    (args: Unit, message: SignableBytes) =>
      Models.Contextual.TickRange
        .Proof(
          blake2b256Bind(Models.Contextual.TickRange.token, message)
        )
        .pure[F]

  def exactMatchProver[F[_]: Applicative]: Prover[F, Unit, Models.Contextual.ExactMatch.Proof] =
    (args: Unit, message: SignableBytes) =>
      Models.Contextual.ExactMatch
        .Proof(
          blake2b256Bind(Models.Contextual.ExactMatch.token, message)
        )
        .pure[F]

  def lessThanProver[F[_]: Applicative]: Prover[F, Unit, Models.Contextual.LessThan.Proof] =
    (args: Unit, message: SignableBytes) =>
      Models.Contextual.LessThan
        .Proof(
          blake2b256Bind(Models.Contextual.LessThan.token, message)
        )
        .pure[F]

  def greaterThanProver[F[_]: Applicative]: Prover[F, Unit, Models.Contextual.GreaterThan.Proof] =
    (args: Unit, message: SignableBytes) =>
      Models.Contextual.GreaterThan
        .Proof(
          blake2b256Bind(Models.Contextual.GreaterThan.token, message)
        )
        .pure[F]

  def equalToProver[F[_]: Applicative]: Prover[F, Unit, Models.Contextual.EqualTo.Proof] =
    (args: Unit, message: SignableBytes) =>
      Models.Contextual.EqualTo
        .Proof(
          blake2b256Bind(Models.Contextual.EqualTo.token, message)
        )
        .pure[F]

  def thresholdProver[F[_]: Applicative]: Prover[F, Set[Option[Proof]], Models.Compositional.Threshold.Proof] =
    (challenges: Set[Option[Proof]], message: SignableBytes) =>
      Models.Compositional.Threshold
        .Proof(
          challenges,
          blake2b256Bind(Models.Compositional.Threshold.token, message)
        )
        .pure[F]

  def notProver[F[_]: Applicative]: Prover[F, Proof, Models.Compositional.Not.Proof] =
    (proof: Proof, message: SignableBytes) =>
      Models.Compositional.Not
        .Proof(
          proof,
          blake2b256Bind(Models.Compositional.Not.token, message)
        )
        .pure[F]

  def andProver[F[_]: Applicative]: Prover[F, (Proof, Proof), Models.Compositional.And.Proof] =
    (proofs: (Proof, Proof), message: SignableBytes) =>
      Models.Compositional.And
        .Proof(
          proofs._1,
          proofs._2,
          blake2b256Bind(Models.Compositional.And.token, message)
        )
        .pure[F]

  def orProver[F[_]: Applicative]: Prover[F, (Proof, Proof), Models.Compositional.Or.Proof] =
    (proofs: (Proof, Proof), message: SignableBytes) =>
      Models.Compositional.Or
        .Proof(
          proofs._1,
          proofs._2,
          blake2b256Bind(Models.Compositional.Or.token, message)
        )
        .pure[F]
}

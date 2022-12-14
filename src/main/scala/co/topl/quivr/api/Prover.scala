package co.topl.quivr.api

import cats.Applicative
import cats.implicits._
import co.topl.crypto.hash.blake2b256
import co.topl.quivr.Tokens
import com.google.protobuf.ByteString
import quivr.models._

import java.nio.charset.StandardCharsets

// Provers create proofs that are bound to the transaction which executes the proof.
//
// This provides a generic way to map all computations (single-step or sigma-protocol)
// into a Fiat-Shamir heuristic if the bind that is used here is unique.
// This seems like it would promote statelessness but I am unsure how.
trait Prover[F[_], A] {

  /**
   * @param args A is product type (tuple) of the inputs needed to satisfy a certain Proposition
   * @param message The unique bytes of the message that the instance of the Proof will be bound to
   * @return a Quivr proof that may be paired with a revealed Proposition in a Verification runtime
   */
  def prove(args: A, message: SignableBytes): F[Proof]
}

object Prover {

  /**
   * @param tag     an identifier of the Operation
   * @param message unique bytes from a transaction that will be bound to the proof
   * @return an array of bytes that is similar to a "signature" for the proof
   */
  private def blake2b256Bind(tag: String, message: SignableBytes): TxBind =
    TxBind(
      ByteString.copyFrom(
        blake2b256.hash(tag.getBytes(StandardCharsets.UTF_8) ++ message.value.toByteArray).value
      )
    )

  def lockedProver[F[_]: Applicative]: Prover[F, Unit] =
    (_: Unit, _: SignableBytes) => Proof().withLocked(Proof.Locked()).pure[F]

  def digestProver[F[_]: Applicative]: Prover[F, Preimage] =
    (preimage: Preimage, message: SignableBytes) =>
      Proof()
        .withDigest(Proof.Digest(blake2b256Bind(Tokens.Digest, message).some, preimage.some))
        .pure[F]

  def signatureProver[F[_]: Applicative]: Prover[F, Witness] =
    (witness: Witness, message: SignableBytes) =>
      Proof()
        .withDigitalSignature(
          Proof.DigitalSignature(blake2b256Bind(Tokens.DigitalSignature, message).some, witness.some)
        )
        .pure[F]

  def heightProver[F[_]: Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableBytes) =>
      Proof()
        .withHeightRange(Proof.HeightRange(blake2b256Bind(Tokens.HeightRange, message).some))
        .pure[F]

  def tickProver[F[_]: Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableBytes) =>
      Proof()
        .withTickRange(Proof.TickRange(blake2b256Bind(Tokens.TickRange, message).some))
        .pure[F]

  def exactMatchProver[F[_]: Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableBytes) =>
      Proof()
        .withExactMatch(Proof.ExactMatch(blake2b256Bind(Tokens.ExactMatch, message).some))
        .pure[F]

  def lessThanProver[F[_]: Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableBytes) =>
      Proof()
        .withLessThan(Proof.LessThan(blake2b256Bind(Tokens.LessThan, message).some))
        .pure[F]

  def greaterThanProver[F[_]: Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableBytes) =>
      Proof()
        .withGreaterThan(Proof.GreaterThan(blake2b256Bind(Tokens.GreaterThan, message).some))
        .pure[F]

  def equalToProver[F[_]: Applicative]: Prover[F, Unit] =
    (_: Unit, message: SignableBytes) =>
      Proof()
        .withEqualTo(Proof.EqualTo(blake2b256Bind(Tokens.EqualTo, message).some))
        .pure[F]

  // TODO: Protobuf's `Proof` type already encapsulates "emptiness", so the Option may be unnecessary here
  def thresholdProver[F[_]: Applicative]: Prover[F, Set[Option[Proof]]] =
    (challenges: Set[Option[Proof]], message: SignableBytes) =>
      Proof()
        .withThreshold(
          Proof.Threshold(
            blake2b256Bind(Tokens.Threshold, message).some,
            challenges.toSeq.map(_.getOrElse(Proof()))
          )
        )
        .pure[F]

  def notProver[F[_]: Applicative]: Prover[F, Proof] =
    (proof: Proof, message: SignableBytes) =>
      Proof()
        .withNot(
          Proof.Not(
            blake2b256Bind(Tokens.Not, message).some,
            proof.some
          )
        )
        .pure[F]

  def andProver[F[_]: Applicative]: Prover[F, (Proof, Proof)] =
    (proofs: (Proof, Proof), message: SignableBytes) =>
      Proof()
        .withAnd(
          Proof.And(
            blake2b256Bind(Tokens.And, message).some,
            proofs._1.some,
            proofs._2.some
          )
        )
        .pure[F]

  def orProver[F[_]: Applicative]: Prover[F, (Proof, Proof)] =
    (proofs: (Proof, Proof), message: SignableBytes) =>
      Proof()
        .withOr(
          Proof.Or(
            blake2b256Bind(Tokens.Or, message).some,
            proofs._1.some,
            proofs._2.some
          )
        )
        .pure[F]
}

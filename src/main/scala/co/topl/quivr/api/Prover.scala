package co.topl.quivr.api

import cats.implicits._
import cats.{Applicative, Monad}
import co.topl.common.Models.{Preimage, Witness}
import co.topl.quivr.{Models, Proof, SignableBytes, TxBind}

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

//  // The 'summoner' or 'materializer' method, when called with a property type returns a type class instance
//  def apply[F[_], A](implicit ev: Prover[F, A]): Prover[F, A] = ev

  def proveAForMessage[F[_]: Monad, A](args: A)(message: SignableBytes): F[Proof] =
    instances.proverInstance[F, A].prove(args, message)

  /**
   * @param tag     an identifier of the Operation
   * @param message unique bytes from a transaction that will be bound to the proof
   * @return an array of bytes that is similar to a "signature" for the proof
   */
  private def bind(tag: String, message: SignableBytes)(f: Array[Byte] => TxBind): TxBind = f(
    tag.getBytes(StandardCharsets.UTF_8) ++ message
  )

  trait Instances {

    private def lockedProver[F[_]: Applicative]: F[Proof] = Models.Primitive.Locked.Proof().pure[F].widen

    private def digestProver[F[_]: Applicative](preimage: Preimage, message: SignableBytes)(
      f:                                                  Array[Byte] => TxBind
    ): F[Proof] =
      Models.Primitive.Digest
        .Proof(
          preimage,
          bind(Models.Primitive.Digest.token, message)(f)
        )
        .pure[F]
        .widen

    private def signatureProver[F[_]: Applicative](witness: Witness, message: SignableBytes)(
      f:                                                    Array[Byte] => TxBind
    ): F[Proof] =
      Models.Primitive.DigitalSignature
        .Proof(
          witness,
          bind(Models.Primitive.DigitalSignature.token, message)(f)
        )
        .pure[F]
        .widen

    private def heightProver[F[_]: Applicative](message: SignableBytes)(f: Array[Byte] => TxBind): F[Proof] =
      Models.Contextual.HeightRange
        .Proof(
          bind(Models.Contextual.HeightRange.token, message)(f)
        )
        .pure[F]
        .widen

    private def tickProver[F[_]: Applicative](message: SignableBytes)(f: Array[Byte] => TxBind): F[Proof] =
      Models.Contextual.HeightRange
        .Proof(
          bind(Models.Contextual.HeightRange.token, message)(f)
        )
        .pure[F]
        .widen

    private def exactMatchProver[F[_]: Applicative](message: SignableBytes)(f: Array[Byte] => TxBind): F[Proof] =
      Models.Contextual.ExactMatch
        .Proof(
          bind(Models.Contextual.ExactMatch.token, message)(f)
        )
        .pure[F]
        .widen

    private def lessThanProver[F[_]: Applicative](message: SignableBytes)(f: Array[Byte] => TxBind): F[Proof] =
      Models.Contextual.LessThan
        .Proof(
          bind(Models.Contextual.LessThan.token, message)(f)
        )
        .pure[F]
        .widen

    private def greaterThanProver[F[_]: Applicative](message: SignableBytes)(f: Array[Byte] => TxBind): F[Proof] =
      Models.Contextual.GreaterThan
        .Proof(
          bind(Models.Contextual.GreaterThan.token, message)(f)
        )
        .pure[F]
        .widen

    private def equalToProver[F[_]: Applicative](message: SignableBytes)(f: Array[Byte] => TxBind): F[Proof] =
      Models.Contextual.EqualTo
        .Proof(
          bind(Models.Contextual.EqualTo.token, message)(f)
        )
        .pure[F]
        .widen

    private def thresholdProver[F[_]: Applicative](challenges: Set[Option[Proof]], message: SignableBytes)(
      f:                                                       Array[Byte] => TxBind
    ): F[Proof] =
      Models.Compositional.Threshold
        .Proof(
          challenges,
          bind(Models.Compositional.Threshold.token, message)(f)
        )
        .pure[F]
        .widen

    private def notProver[F[_]: Applicative](proof: Proof, message: SignableBytes)(
      f:                                            Array[Byte] => TxBind
    ): F[Proof] =
      Models.Compositional.Not
        .Proof(
          proof,
          bind(Models.Compositional.Not.token, message)(f)
        )
        .pure[F]
        .widen

    private def andProver[F[_]: Applicative](left: Proof, right: Proof, message: SignableBytes)(
      f:                                           Array[Byte] => TxBind
    ): F[Proof] =
      Models.Compositional.And
        .Proof(
          left,
          right,
          bind(Models.Compositional.And.token, message)(f)
        )
        .pure[F]
        .widen

    private def orProver[F[_]: Applicative](left: Proof, right: Proof, message: SignableBytes)(
      f:                                          Array[Byte] => TxBind
    ): F[Proof] =
      Models.Compositional.Or
        .Proof(
          left,
          right,
          bind(Models.Compositional.Or.token, message)(f)
        )
        .pure[F]
        .widen

    def proverInstance[F[_]: Monad, A]: Prover[F, A] = {
      // todo: change to sha3?
      val instanceBind = (x: Array[Byte]) => x.tail

      (args: A, message: SignableBytes) =>
        args match {
          case Models.Primitive.Locked.token =>
            lockedProver[F]
          case (Models.Primitive.Digest.token, t: Preimage) =>
            digestProver[F](t, message)(instanceBind)
          case (Models.Primitive.DigitalSignature.token, t: Witness) =>
            signatureProver[F](t, message)(instanceBind)
          case Models.Contextual.HeightRange.token =>
            heightProver[F](message)(instanceBind)
          case Models.Contextual.TickRange.token =>
            tickProver[F](message)(instanceBind)
          case Models.Contextual.ExactMatch.token =>
            exactMatchProver[F](message)(instanceBind)
          case Models.Contextual.LessThan.token =>
            lessThanProver[F](message)(instanceBind)
          case Models.Contextual.GreaterThan.token =>
            greaterThanProver[F](message)(instanceBind)
          case Models.Contextual.EqualTo.token =>
            equalToProver[F](message)(instanceBind)
          case (Models.Compositional.Threshold.token, t: Set[Option[Proof]]) =>
            thresholdProver[F](t, message)(instanceBind)
          case (Models.Compositional.Not.token, t: Proof) =>
            notProver[F](t, message)(instanceBind)
          case (Models.Compositional.And.token, l: Proof, r: Proof) =>
            andProver[F](l, r, message)(instanceBind)
          case (Models.Compositional.Or.token, l: Proof, r: Proof) =>
            orProver[F](l, r, message)(instanceBind)
          case _ =>
            lockedProver[F]
        }
    }
  }

  object instances extends Instances
}

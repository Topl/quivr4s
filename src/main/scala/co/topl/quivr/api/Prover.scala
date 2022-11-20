package co.topl.quivr

import cats.implicits._
import cats.{Applicative, Monad}
import co.topl.crypto.hash.blake2b256
import co.topl.common

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
  def prove(args: A, message: SignableTxBytes): F[Proof]
}

object Prover {

  // The 'summoner' or 'materializer' method, when called with a property type returns a type class instance
  def apply[F[_], A](implicit ev: Prover[F, A]): Prover[F, A] = ev

  def proveAForMessage[F[_]: Monad, A](args: A)(message: SignableTxBytes): F[Proof] =
    instances.proverInstance[F, A].prove(args, message)

  /**
   * @param tag     an identifier of the Operation
   * @param message unique bytes from a transaction that will be bound to the proof
   * @return an array of bytes that is similar to a "signature" for the proof
   */
  private def bind(tag: Byte, message: SignableTxBytes)(f: Array[Byte] => TxBind): TxBind = f(tag +: message)

  trait Instances {

    private def lockedProver[F[_]: Applicative]: F[Proof] = Models.Primitive.Locked.Proof().pure[F].widen

    private def digestProver[F[_]: Applicative](preimage: common.Preimage, salt: Long, message: SignableTxBytes)(
      f:                                                  Array[Byte] => TxBind
    ): F[Proof] =
      Models.Primitive.Digest
        .Proof(
          preimage,
          salt,
          bind(Models.Primitive.Digest.token, message)(f)
        )
        .pure[F]
        .widen

    private def signatureProver[F[_]: Applicative](witness: common.Witness, message: SignableTxBytes)(
      f:                                                    Array[Byte] => TxBind
    ): F[Proof] =
      Models.Primitive.DigitalSignature
        .Proof(
          witness,
          bind(Models.Primitive.DigitalSignature.token, message)(f)
        )
        .pure[F]
        .widen

    private def heightProver[F[_]: Applicative](message: SignableTxBytes)(f: Array[Byte] => TxBind): F[Proof] =
      Models.Contextual.HeightRange
        .Proof(
          bind(Models.Contextual.HeightRange.token, message)(f)
        )
        .pure[F]
        .widen

    private def tickProver[F[_]: Applicative](message: SignableTxBytes)(f: Array[Byte] => TxBind): F[Proof] =
      Models.Contextual.HeightRange
        .Proof(
          bind(Models.Contextual.HeightRange.token, message)(f)
        )
        .pure[F]
        .widen

    private def exactMatchProver[F[_]: Applicative](message: SignableTxBytes)(f: Array[Byte] => TxBind): F[Proof] =
      Models.Contextual.ExactMatch
        .Proof(
          bind(Models.Contextual.ExactMatch.token, message)(f)
        )
        .pure[F]
        .widen

    private def lessThanProver[F[_]: Applicative](message: SignableTxBytes)(f: Array[Byte] => TxBind): F[Proof] =
      Models.Contextual.LessThan
        .Proof(
          bind(Models.Contextual.LessThan.token, message)(f)
        )
        .pure[F]
        .widen

    private def greaterThanProver[F[_]: Applicative](message: SignableTxBytes)(f: Array[Byte] => TxBind): F[Proof] =
      Models.Contextual.GreaterThan
        .Proof(
          bind(Models.Contextual.GreaterThan.token, message)(f)
        )
        .pure[F]
        .widen

    private def equalToProver[F[_]: Applicative](message: SignableTxBytes)(f: Array[Byte] => TxBind): F[Proof] =
      Models.Contextual.EqualTo
        .Proof(
          bind(Models.Contextual.EqualTo.token, message)(f)
        )
        .pure[F]
        .widen

    private def thresholdProver[F[_]: Applicative](challenges: Set[Option[Proof]], message: SignableTxBytes)(
      f:                                                       Array[Byte] => TxBind
    ): F[Proof] =
      Models.Compositional.Threshold
        .Proof(
          challenges,
          bind(Models.Compositional.Threshold.token, message)(f)
        )
        .pure[F]
        .widen

    private def notProver[F[_]: Applicative](proof: Proof, message: SignableTxBytes)(
      f:                                            Array[Byte] => TxBind
    ): F[Proof] =
      Models.Compositional.Not
        .Proof(
          proof,
          bind(Models.Compositional.Not.token, message)(f)
        )
        .pure[F]
        .widen

    private def andProver[F[_]: Applicative](left: Proof, right: Proof, message: SignableTxBytes)(
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

    private def orProver[F[_]: Applicative](left: Proof, right: Proof, message: SignableTxBytes)(
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

    implicit def proverInstance[F[_]: Monad, A]: Prover[F, A] = {
      // todo: change to sha3?
      val instanceBind = (x: Array[Byte]) => blake2b256.hash(x).value

      (args: A, message: SignableTxBytes) =>
        args match {
          case t: Byte if t == Models.Primitive.Locked.token => lockedProver[F]
          case t: (Byte, common.Preimage, Long) if t._1 == Models.Primitive.Digest.token =>
            digestProver(t._2, message)(instanceBind)
          case t: (Byte, common.Witness) if t._1 == Models.Primitive.DigitalSignature.token =>
            signatureProver(t._2, message)(instanceBind)
          case t: Byte if t == Models.Contextual.HeightRange.token => heightProver(message)(instanceBind)
          case t: Byte if t == Models.Contextual.TickRange.token   => tickProver(message)(instanceBind)
          // case t: Byte if t == Models.Contextual.ExactMatch.token  => exactMatchProver(message)(instanceBind)
          // case t: Byte if t == Models.Contextual.LessThan.token    => lessThanProver(message)(instanceBind)
          // case t: Byte if t == Models.Contextual.GreaterThan.token => greaterThanProver(message)(instanceBind)
          // case t: Byte if t == Models.Contextual.EqualTo.token     => equalToProver(message)(instanceBind)
          case t: (Byte, Set[Option[Proof]]) if t._1 == Models.Compositional.Threshold.token =>
            thresholdProver(t._2, message)(instanceBind)
          case t: (Byte, Proof) if t._1 == Models.Compositional.Not.token =>
            notProver(t._2, message)(instanceBind)
          case t: (Byte, Proof, Proof) if t._1 == Models.Compositional.And.token =>
            andProver(t._2, t._3, message)(instanceBind)
          case t: (Byte, Proof, Proof) if t._1 == Models.Compositional.Or.token =>
            orProver(t._2, t._3, message)(instanceBind)
          case _ => lockedProver[F]
        }
    }
  }

  object instances extends Instances
}

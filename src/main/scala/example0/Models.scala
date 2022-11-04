package example0

import co.topl.crypto.hash._
import cats.Applicative
import cats.implicits._

case class Box(
  image: Box.Predicate.Image,
  value: Box.Value,
  datum: Option[Quivr.Datums.Box]
)

object Box {
  sealed abstract class Value

  object Values {
    case class Token(quantity: Long) extends Box.Value
    case class Asset(label: Byte, quantity: Long, metadata: Array[Byte]) extends Box.Value
  }

  case class Predicate(conditions: List[Quivr.Proposition], threshold: Long)

  object Predicate {
    case class Image(digest: Array[Byte])
  }
  //   case class Id(image: Image)

  //   def idFromImage(image: Predicate.Image): Predicate.Id = Id(blake2b256.hash(image.digest))
  // }

  case class Attestation()
  object Attestation {}
}

object Quivr {

  object Operations {
    trait Locked
    trait Digest
    trait DigitalSignature
    trait HeightRange
    trait SlotRange
    trait ExactMatch
    trait Threshold
    trait Not
  }

  // Propositions represent challenges that must be satisifed
  sealed abstract class Proposition

  // For each Proposition there is a corresponding Proof that
  // can be constructed to satisfy the given Proposition
  sealed abstract class Proof(bindToTransaction: Array[Byte])

  // Proposers create Propositions from a tuple of arguments (or single argument) of type A.
  trait Proposer[F[_], A] {
    def propose(args: A): F[Quivr.Proposition]
  }

  // This represents the native apply (constructor) methods for the returned Proposition
  // todo: we probably need to introduce another layer of type guarantees or validation for generic Array[Byte] types
  object Proposer {

    def LockedProposer[F[_]: Applicative, A]: Proposer[F, Array[Byte]] =
      new Proposer[F, Array[Byte]] {

        def propose(data: Array[Byte]): F[Quivr.Proposition] =
          Models.Primitive.Locked.Proposition(data).pure[F].widen
      }

    def digestProposer[F[_]: Applicative, A]: Proposer[F, Array[Byte]] =
      new Proposer[F, Array[Byte]] {

        def propose(digest: Array[Byte]): F[Quivr.Proposition] =
          Models.Primitive.Digest.Proposition(digest).pure[F].widen
      }

    def signatureProposer[F[_]: Applicative, A]: Proposer[F, Array[Byte]] =
      new Proposer[F, Array[Byte]] {

        def propose(vk: Array[Byte]): F[Quivr.Proposition] =
          Models.Primitive.DigitalSignature.Proposition(vk).pure[F].widen
      }

    def heightProposer[F[_]: Applicative, A]: Proposer[F, (Long, Long)] =
      new Proposer[F, (Long, Long)] {

        def propose(args: (Long, Long)): F[Quivr.Proposition] =
          Models.Contextual.Height.Proposition(args._1, args._2).pure[F].widen
      }

    def slotProposer[F[_]: Applicative, A]: Proposer[F, (Long, Long)] =
      new Proposer[F, (Long, Long)] {

        def propose(args: (Long, Long)): F[Quivr.Proposition] =
          Models.Contextual.Slot.Proposition(args._1, args._2).pure[F].widen
      }

    // def exactMatchProposer

    def thresholdProposer[F[_]: Applicative, A]: Proposer[
      F,
      (Array[Quivr.Proposition], Int, Models.Compositional.Threshold.BooleanOp)
    ] =
      new Proposer[F, (Array[Quivr.Proposition], Int, Models.Compositional.Threshold.BooleanOp)] {

        def propose(
          args: (
            Array[Quivr.Proposition],
            Int,
            Models.Compositional.Threshold.BooleanOp
          )
        ): F[Quivr.Proposition] =
          Models.Compositional.Threshold
            .Proposition(args._1, args._2, args._3)
            .pure[F]
            .widen
      }

    def notProposer[F[_]: Applicative]: Proposer[F, Quivr.Proposition] =
      new Proposer[F, Quivr.Proposition] {

        def propose(
          proposition: Quivr.Proposition
        ): F[Quivr.Proposition] =
          Models.Compositional.Not
            .Proposition(proposition)
            .pure[F]
            .widen
      }
  }

  // Provers create proofs that are bound to the transactions which execute the proof.
  //
  // This provides a generic way to map all computations (single-step or sigma-protocol)
  // into a Fiat-Shamir hueristic if the bind that is used here is unique.
  // This seems like it would promote statelessness but I am unsure how.
  trait Prover[F[_], A] {
    def prove(args: A)(message: Array[Byte]): F[Quivr.Proof]
  }

  object Prover {

    /**
     * @param tag
     *   an identifier of the Operation
     * @param message
     *   unique bytes from a transaction that will be bound to the proof
     * @return
     *   an array of bytes that is similar to a "signature" for the proof
     */
    def bind(tag: Byte, message: Array[Byte]): Array[Byte] =
      blake2b256.hash(message :+ tag).value

    def lockedProver[F[_]: Applicative]: Prover[F, Unit] =
      new Prover[F, Unit] {

        def prove(args: Unit)(message: Array[Byte]): F[Proof] = Models.Primitive.Locked
          .Proof(
            bind(Models.Primitive.Locked.token, message)
          )
          .pure[F]
          .widen

      }

    def digestProver[F[_]: Applicative]: Prover[F, Array[Byte]] =
      new Prover[F, Array[Byte]] {

        def prove(preimage: Array[Byte])(message: Array[Byte]): F[Quivr.Proof] =
          Models.Primitive.Digest
            .Proof(
              preimage,
              bind(Models.Primitive.Digest.token, message)
            )
            .pure[F]
            .widen
      }

    def signatureProver[F[_]: Applicative]: Prover[F, Array[Byte]] =
      new Prover[F, Array[Byte]] {

        def prove(sk: Array[Byte])(message: Array[Byte]): F[Proof] =
          Models.Primitive.DigitalSignature
            .Proof(
              bind(Models.Primitive.DigitalSignature.token, sk ++ message)
            )
            .pure[F]
            .widen

      }

    def heightProver[F[_]: Applicative]: Prover[F, Unit] =
      new Prover[F, Unit] {

        def prove(args: Unit)(message: Array[Byte]): F[Quivr.Proof] =
          Models.Contextual.Height
            .Proof(bind(Models.Contextual.Height.token, message))
            .pure[F]
            .widen
      }

    def slotProver[F[_]: Applicative]: Prover[F, Unit] =
      new Prover[F, Unit] {

        def prove(args: Unit)(message: Array[Byte]): F[Quivr.Proof] =
          Models.Contextual.Height
            .Proof(bind(Models.Contextual.Height.token, message))
            .pure[F]
            .widen
      }

    // def exactMatchProver[F[_]: Applicative]: Prover[F, Unit] =
    //   new Prover[F, Unit] {}

    def thresholdProver[F[_]: Applicative]: Prover[F, Array[Option[Quivr.Proof]]] =
      new Prover[F, Array[Option[Quivr.Proof]]] {

        def prove(challenges: Array[Option[Quivr.Proof]])(message: Array[Byte]): F[Quivr.Proof] =
          Models.Compositional.Threshold
            .Proof(challenges, bind(Models.Compositional.Threshold.token, message))
            .pure[F]
            .widen
      }

    def notProver[F[_]: Applicative]: Prover[F, Unit] =
      new Prover[F, Unit] {

        def prove(args: Unit)(message: Array[Byte]): F[Quivr.Proof] =
          Models.Compositional.Not
            .Proof(bind(Models.Compositional.Not.token, message))
            .pure[F]
            .widen
      }
  }

  /**
   * A Verifier evaluates whether a given Proof satisfies a certain Proposition
   */
  abstract class Verifier[F[_], C <: Quivr.Proposition, R <: Quivr.Proof] {

    def verify(proposition: C, proof: R)(
      ctx:                  EvaluationContext
    ): F[Boolean]
  }

  object Verifier {

    // todo: how to pass which of these failed back up?
    def digestVerifier[F[_]: Applicative]: Verifier[
      F,
      Models.Primitive.Digest.Proposition,
      Models.Primitive.Digest.Proof
    ] =
      new Verifier[
        F,
        Models.Primitive.Digest.Proposition,
        Models.Primitive.Digest.Proof
      ] {

        def verify(
          proposition: Models.Primitive.Digest.Proposition,
          proof:       Models.Primitive.Digest.Proof
        )(ctx:         EvaluationContext): F[Boolean] = {
          val msgAuth =
            Prover.bind(
              Models.Primitive.Digest.token,
              ctx.iotx.signableBytes
            ) sameElements proof.transactionBind

          val evalAuth = (blake2b256
            .hash(proof.preimage)
            .value sameElements proposition.digest)

          (msgAuth && evalAuth).pure[F]
        }
      }

    def heightVerifier[F[_]: Applicative]: Verifier[
      F,
      Models.Contextual.Height.Proposition,
      Models.Contextual.Height.Proof
    ] =
      new Verifier[
        F,
        Models.Contextual.Height.Proposition,
        Models.Contextual.Height.Proof
      ] {

        override def verify(
          proposition: Models.Contextual.Height.Proposition,
          proof:       Models.Contextual.Height.Proof
        )(ctx:         EvaluationContext): F[Boolean] = {
          val msgAuth =
            Prover.bind(
              Models.Contextual.Height.token,
              ctx.iotx.signableBytes
            ) sameElements proof.transactionBind

          val evalAuth =
            proposition.min <= ctx.header.height && ctx.header.height <= proposition.max

          (msgAuth && evalAuth).pure[F]
        }
      }
  }

  sealed abstract class Datum

  object Datums {
    case class Header(slot: Long, height: Long) extends Datum
    case class Body(root: Array[Byte]) extends Datum
    case class IoTx(signableBytes: Array[Byte]) extends Datum
    case class Box(metadata: Array[Byte]) extends Datum
  }

  case class EvaluationContext(
    header: Datums.Header,
    body:   Datums.Body,
    iotx:   Datums.IoTx,
    box:    Datums.Box
  )
}

object Models {

  object Primitive {

    object Locked {
      val token: Byte = 0: Byte
      case class Proposition(data: Array[Byte]) extends Quivr.Proposition with Quivr.Operations.Locked
      case class Proof(transactionBind: Array[Byte]) extends Quivr.Proof(transactionBind) with Quivr.Operations.Locked
    }

    object Digest {
      val token: Byte = 1: Byte
      case class Proposition(digest: Array[Byte]) extends Quivr.Proposition with Quivr.Operations.Digest

      case class Proof(preimage: Array[Byte], transactionBind: Array[Byte])
          extends Quivr.Proof(transactionBind)
          with Quivr.Operations.Digest
    }

    object DigitalSignature {
      val token: Byte = 2: Byte
      case class Proposition(vk: Array[Byte]) extends Quivr.Proposition with Quivr.Operations.DigitalSignature

      case class Proof(transactionBind: Array[Byte])
          extends Quivr.Proof(transactionBind)
          with Quivr.Operations.DigitalSignature
    }
  }

  object Contextual {

    object Height {
      val token: Byte = -1: Byte
      case class Proposition(min: Long, max: Long) extends Quivr.Proposition with Quivr.Operations.HeightRange

      case class Proof(transactionBind: Array[Byte])
          extends Quivr.Proof(transactionBind)
          with Quivr.Operations.HeightRange
    }

    object Slot {
      val token: Byte = -2: Byte
      case class Proposition(min: Long, max: Long) extends Quivr.Proposition with Quivr.Operations.HeightRange

      case class Proof(transactionBind: Array[Byte])
          extends Quivr.Proof(transactionBind)
          with Quivr.Operations.HeightRange
    }

    object ExactMatch {
      val token: Byte = -3: Byte
    }

  }

  object Compositional {

    object Not {
      val token: Byte = 126: Byte
      case class Proposition(proposition: Quivr.Proposition) extends Quivr.Proposition with Quivr.Operations.Not
      case class Proof(transactionBind: Array[Byte]) extends Quivr.Proof(transactionBind) with Quivr.Operations.Not
    }

    object Threshold {
      sealed abstract class BooleanOp
      case object And extends BooleanOp
      case object Or extends BooleanOp

      val token: Byte = 127: Byte

      case class Proposition(
        challenges: Array[Quivr.Proposition],
        threshold:  Int,
        connector:  BooleanOp
      ) extends Quivr.Proposition
          with Quivr.Operations.Threshold

      case class Proof(
        responses:       Array[Option[Quivr.Proof]],
        transactionBind: Array[Byte]
      ) extends Quivr.Proof(transactionBind)
          with Quivr.Operations.Threshold
    }
  }

}

object TestModels {
  import java.security.SecureRandom

  def main(args: Array[String]): Unit = {}

}

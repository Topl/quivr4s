package co.topl

import co.topl.Crypto.{VerificationKey, Witness}

package object quivr {

  type SignableTxBytes = Array[Byte]
  type TxBind = Array[Byte]

  // Propositions represent challenges that must be satisfied
  sealed abstract class Proposition

  // For each Proposition there is a corresponding Proof that can be constructed to satisfy the given Proposition
  sealed abstract class Proof(val bindToTransaction: TxBind)

  object Operations {
    trait Locked

    trait Digest

    trait DigitalSignature

    trait HeightRange

    trait TickRange

    trait ExactMatch

    trait GreaterThan

    trait LessThan

    trait Threshold

    trait Not
  }

  object Evaluation {
    trait Datum

    trait IncludesHeight extends Datum {
      def height: Long
    }

    trait SignatureVerifier {
      def verify(vk: VerificationKey, sig: Witness, msg: Array[Byte]): Boolean
    }

    abstract class Output(val value: Array[Byte])

    trait Ledger[F[_]] {
      val ledgerValue: Output
      def parseValue[T](f:    Output => T): T
      def evaluate[A, B](arg: A)(f: Output => Boolean): Boolean
    }

    trait DynamicContext[F[_]] {
      val datums: Map[String, Datum]
      val signingRoutines: Map[String, SignatureVerifier]
      val ledgers: Map[String, Ledger[F]]

      def useDatum[T](label: String)(f: Datum => T): F[T]

      def heightOf(label: String): Option[Long] =
        datums.get(label).map { case v: IncludesHeight =>
          v.height
        }

      def signatureVerify(routine: String)(vk: VerificationKey, sig: Witness, msg: Array[Byte]): Boolean =
        signingRoutines.get(routine).fold(false)(_.verify(vk, sig, msg))

      def useLedger[A](label: String, arg: A)(f: Output => Boolean): Boolean =
        ledgers.get(label).fold(false) { l =>
          l.evaluate(arg)(f)
        }

      def signableBytes: F[SignableTxBytes]

      def currentTick: F[Long]
    }
  }

  object Models {

    object Primitive {

      object Locked {
        val token: Byte = 0: Byte

        final case class Proposition(
          data: Option[Array[Byte]]
        ) extends quivr.Proposition
            with quivr.Operations.Locked

        final case class Proof(
          transactionBind: TxBind
        ) extends quivr.Proof(transactionBind)
            with quivr.Operations.Locked
      }

      object Digest {
        val token: Byte = 1: Byte

        final case class Proposition(
          digest: Array[Byte]
        ) extends quivr.Proposition
            with quivr.Operations.Digest

        final case class Proof(
          preimage:        Array[Byte],
          transactionBind: TxBind
        ) extends quivr.Proof(transactionBind)
            with quivr.Operations.Digest
      }

      object DigitalSignature {
        val token: Byte = 2: Byte

        final case class Proposition(
          routine: String,
          vk:      VerificationKey
        ) extends quivr.Proposition
            with quivr.Operations.DigitalSignature

        final case class Proof(
          witness:         Witness,
          transactionBind: TxBind
        ) extends quivr.Proof(transactionBind)
            with quivr.Operations.DigitalSignature
      }
    }

    object Contextual {

      object HeightRange {
        val token: Byte = -1: Byte

        final case class Proposition(
          location: String,
          min:      Long,
          max:      Long
        ) extends quivr.Proposition
            with quivr.Operations.HeightRange

        final case class Proof(
          transactionBind: TxBind
        ) extends quivr.Proof(transactionBind)
            with quivr.Operations.HeightRange
      }

      object TickRange {
        val token: Byte = -2: Byte

        final case class Proposition(
          min: Long,
          max: Long
        ) extends quivr.Proposition
            with quivr.Operations.TickRange

        final case class Proof(
          transactionBind: TxBind
        ) extends quivr.Proof(transactionBind)
            with quivr.Operations.TickRange
      }

      object ExactMatch {
        val token: Byte = -3: Byte
      }

    }

    object Compositional {

      object Not {
        val token: Byte = 126: Byte

        final case class Proposition(
          proposition: quivr.Proposition
        ) extends quivr.Proposition
            with quivr.Operations.Not

        final case class Proof(
          proof:           quivr.Proof,
          transactionBind: TxBind
        ) extends quivr.Proof(transactionBind)
            with quivr.Operations.Not
      }

      object Threshold {
        sealed abstract class BooleanOp
        case object And extends BooleanOp
        case object Or extends BooleanOp

        val token: Byte = 127: Byte

        final case class Proposition(
          challenges: Array[quivr.Proposition],
          threshold:  Int,
          connector:  BooleanOp
        ) extends quivr.Proposition
            with quivr.Operations.Threshold

        final case class Proof(
          responses:       Array[Option[quivr.Proof]],
          transactionBind: TxBind
        ) extends quivr.Proof(transactionBind)
            with quivr.Operations.Threshold
      }
    }

  }
}

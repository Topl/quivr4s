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

    trait LessThan

    trait GreaterThan

    trait EqualTo

    trait Threshold

    trait Not

    trait And

    trait Or
  }

  object Evaluation {
    trait Datum

    trait IncludesHeight extends Datum {
      def height: Long
    }

    trait DigestVerifier {
      def verify(preimage: Array[Byte], digest: Array[Byte]): Boolean
    }

    trait SignatureVerifier {
      def verify(vk: VerificationKey, sig: Witness, msg: Array[Byte]): Boolean
    }

    abstract class Data(val bytes: Array[Byte])

    trait Interface {
      val data: Data
      def parse[T](f: Data => Option[T]): Option[T] = f(data)
    }

    trait DynamicContext[F[_]] {
      val datums: Map[String, Datum]
      val interfaces: Map[String, Interface]

      val signingRoutines: Map[String, SignatureVerifier]
      val hashingRoutines: Map[String, DigestVerifier]

      def signableBytes: F[SignableTxBytes]

      def currentTick: F[Long]

      def heightOf(label: String): Option[Long] =
        datums.get(label).map { case v: IncludesHeight =>
          v.height
        }

      def digestVerify(routine: String)(preimage: Array[Byte], digest: Array[Byte]): Boolean =
        hashingRoutines.get(routine).fold(false)(_.verify(preimage, digest))

      def signatureVerify(routine: String)(vk: VerificationKey, sig: Witness, msg: Array[Byte]): Boolean =
        signingRoutines.get(routine).fold(false)(_.verify(vk, sig, msg))

      def useInterface[T](label: String)(f: Data => Option[T])(ff: T => Boolean): Boolean =
        interfaces
          .get(label)
          .flatMap { in =>
            in.parse[T](f).map { value =>
              ff(value)
            }
          }
          .fold(false)(identity)

      def exactMatch(label: String, compareTo: Array[Byte]): Boolean =
        useInterface(label)(d => Some(d.bytes))(b => b sameElements compareTo)

      def lessThan(label: String, compareTo: Long): Boolean =
        useInterface(label)(d => Some(BigInt(d.bytes)))(n => n.longValue < compareTo)

      def greaterThan(label: String, compareTo: Long): Boolean =
        useInterface(label)(d => Some(BigInt(d.bytes)))(n => n.longValue > compareTo)

      def equalTo(label: String, compareTo: Long): Boolean =
        useInterface(label)(d => Some(BigInt(d.bytes)))(n => n.longValue == compareTo)
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
          routine: String,
          digest:  Array[Byte]
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
          chain: String,
          min:   Long,
          max:   Long
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

        final case class Proposition(label: String, compareTo: Array[Byte])
            extends quivr.Proposition
            with quivr.Operations.ExactMatch

        final case class Proof(transactionBind: TxBind)
            extends quivr.Proof(transactionBind)
            with quivr.Operations.ExactMatch
      }

      object LessThan {
        val token: Byte = -4: Byte

        final case class Proposition(label: String, compareTo: Long)
            extends quivr.Proposition
            with quivr.Operations.LessThan

        final case class Proof(transactionBind: TxBind)
            extends quivr.Proof(transactionBind)
            with quivr.Operations.LessThan
      }

      object GreaterThan {
        val token: Byte = -5: Byte

        final case class Proposition(label: String, compareTo: Long)
            extends quivr.Proposition
            with quivr.Operations.GreaterThan

        final case class Proof(transactionBind: TxBind)
            extends quivr.Proof(transactionBind)
            with quivr.Operations.GreaterThan
      }

      object EqualTo {
        val token: Byte = -6: Byte

        final case class Proposition(label: String, compareTo: Long)
            extends quivr.Proposition
            with quivr.Operations.EqualTo

        final case class Proof(transactionBind: TxBind)
            extends quivr.Proof(transactionBind)
            with quivr.Operations.EqualTo
      }

    }

    object Compositional {

      object Threshold {
        val token: Byte = 127: Byte

        final case class Proposition(
          challenges: Array[quivr.Proposition],
          threshold:  Int
        ) extends quivr.Proposition
            with quivr.Operations.Threshold

        final case class Proof(
          responses:       Array[Option[quivr.Proof]],
          transactionBind: TxBind
        ) extends quivr.Proof(transactionBind)
            with quivr.Operations.Threshold
      }

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

      object And {
        val token: Byte = 125: Byte

        final case class Proposition(
          left:  quivr.Proposition,
          right: quivr.Proposition
        ) extends quivr.Proposition
            with quivr.Operations.And

        final case class Proof(
          left:            quivr.Proof,
          right:           quivr.Proof,
          transactionBind: TxBind
        ) extends quivr.Proof(transactionBind)
            with quivr.Operations.And
      }

      object Or {
        val token: Byte = 124: Byte

        final case class Proposition(
          left:  quivr.Proposition,
          right: quivr.Proposition
        ) extends quivr.Proposition
            with quivr.Operations.Or

        final case class Proof(
          left:            quivr.Proof,
          right:           quivr.Proof,
          transactionBind: TxBind
        ) extends quivr.Proof(transactionBind)
            with quivr.Operations.Or
      }
    }
  }
}

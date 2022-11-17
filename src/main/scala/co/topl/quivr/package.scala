package co.topl

import cats.Monad
import cats.data.EitherT

package object quivr {
  type SignableTxBytes = Array[Byte]
  type TxBind = Array[Byte]

  // Propositions represent challenges that must be satisfied
  sealed abstract class Proposition

  // For each Proposition there is a corresponding Proof that can be constructed to satisfy the given Proposition
  sealed abstract class Proof(val tag: Byte, val bindToTransaction: TxBind)

  // The operations offered via the Quivr DSL
  object Operations {
  sealed abstract class Locked

  sealed abstract class Digest

  sealed abstract class DigitalSignature

  sealed abstract class HeightRange

  sealed abstract class TickRange

  sealed abstract class MustInclude

  sealed abstract class ExactMatch

  sealed abstract class LessThan

  sealed abstract class GreaterThan

  sealed abstract class EqualTo

  sealed abstract class Threshold

  sealed abstract class Not

  sealed abstract class And

  sealed abstract class Or
}



  object Models {

    object Primitive {

      object Locked {
        val token: Byte = 0: Byte

        final case class Proposition(
          data: Option[User.Data]
        ) extends quivr.Proposition
            with quivr.Operations.Locked

        final case class Proof() extends quivr.Proof(token, Array(0: Byte)) with quivr.Operations.Locked
      }

      object Digest {
        val token: Byte = 1: Byte

        final case class Proposition(
          routine: String,
          digest:  User.Digest
        ) extends quivr.Proposition
            with quivr.Operations.Digest

        final case class Proof(
          preimage:        User.Preimage,
          transactionBind: TxBind
        ) extends quivr.Proof(token, transactionBind)
            with quivr.Operations.Digest
      }

      object DigitalSignature {
        val token: Byte = 2: Byte

        final case class Proposition(
          routine: String,
          vk:      User.VerificationKey
        ) extends quivr.Proposition
            with quivr.Operations.DigitalSignature

        final case class Proof(
          witness:         User.Witness,
          transactionBind: TxBind
        ) extends quivr.Proof(token, transactionBind)
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
        ) extends quivr.Proof(token, transactionBind)
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
        ) extends quivr.Proof(token, transactionBind)
            with quivr.Operations.TickRange
      }

      object ExactMatch {
        val token: Byte = -3: Byte

        final case class Proposition(label: String, compareTo: Array[Byte])
            extends quivr.Proposition
            with quivr.Operations.ExactMatch

        final case class Proof(transactionBind: TxBind)
            extends quivr.Proof(token, transactionBind)
            with quivr.Operations.ExactMatch
      }

      object LessThan {
        val token: Byte = -4: Byte

        final case class Proposition(label: String, compareTo: Long)
            extends quivr.Proposition
            with quivr.Operations.LessThan

        final case class Proof(transactionBind: TxBind)
            extends quivr.Proof(token, transactionBind)
            with quivr.Operations.LessThan
      }

      object GreaterThan {
        val token: Byte = -5: Byte

        final case class Proposition(label: String, compareTo: Long)
            extends quivr.Proposition
            with quivr.Operations.GreaterThan

        final case class Proof(transactionBind: TxBind)
            extends quivr.Proof(token, transactionBind)
            with quivr.Operations.GreaterThan
      }

      object EqualTo {
        val token: Byte = -6: Byte

        final case class Proposition(label: String, compareTo: Long)
            extends quivr.Proposition
            with quivr.Operations.EqualTo

        final case class Proof(transactionBind: TxBind)
            extends quivr.Proof(token, transactionBind)
            with quivr.Operations.EqualTo
      }

    }

    object Compositional {

      object Threshold {
        val token: Byte = 127: Byte

        final case class Proposition(
          challenges: Set[quivr.Proposition],
          threshold:  Int
        ) extends quivr.Proposition
            with quivr.Operations.Threshold

        final case class Proof(
          responses:       Set[Option[quivr.Proof]],
          transactionBind: TxBind
        ) extends quivr.Proof(token, transactionBind)
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
        ) extends quivr.Proof(token, transactionBind)
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
        ) extends quivr.Proof(token, transactionBind)
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
        ) extends quivr.Proof(token, transactionBind)
            with quivr.Operations.Or
      }
    }
  }
}

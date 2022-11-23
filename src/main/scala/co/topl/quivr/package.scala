package co.topl

package object quivr {
  type SignableBytes = Array[Byte] // MUST NOT include proof bytes
  type TxBind = Array[Byte]

  // Propositions represent challenges that must be satisfied
  sealed abstract class Proposition

  // For each Proposition there is a corresponding Proof that can be constructed to satisfy the given Proposition
  sealed abstract class Proof(val tag: String, val bindToTransaction: TxBind)

  // The operations offered via the Quivr DSL
  object Operations {
    trait Locked

    trait Digest

    trait DigitalSignature

    trait HeightRange

    trait TickRange

    trait MustInclude

    trait ExactMatch

    trait LessThan

    trait GreaterThan

    trait EqualTo

    trait Threshold

    trait Not

    trait And

    trait Or
  }

  object Models {

    object Primitive {

      object Locked {
        val token: String = "locked"

        final case class Proposition(
          data: Option[common.Data]
        ) extends quivr.Proposition
            with quivr.Operations.Locked

        final case class Proof() extends quivr.Proof(token, Array(0: Byte)) with quivr.Operations.Locked
      }

      object Digest {
        val token: String = "digest"

        final case class Proposition(
          routine: String,
          digest:  common.Digest
        ) extends quivr.Proposition
            with quivr.Operations.Digest

        final case class Proof(
          preimage:        common.Preimage,
          transactionBind: TxBind
        ) extends quivr.Proof(token, transactionBind)
            with quivr.Operations.Digest
      }

      object DigitalSignature {
        val token: String = "digital_signature"

        final case class Proposition(
          routine: String,
          vk:      common.VerificationKey
        ) extends quivr.Proposition
            with quivr.Operations.DigitalSignature

        final case class Proof(
          witness:         common.Witness,
          transactionBind: TxBind
        ) extends quivr.Proof(token, transactionBind)
            with quivr.Operations.DigitalSignature
      }
    }

    object Contextual {

      object HeightRange {
        val token: String = "height_range"

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
        val token: String = "tick_range"

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
        val token: String = "exact_match"

        final case class Proposition(label: String, compareTo: Array[Byte])
            extends quivr.Proposition
            with quivr.Operations.ExactMatch

        final case class Proof(transactionBind: TxBind)
            extends quivr.Proof(token, transactionBind)
            with quivr.Operations.ExactMatch
      }

      object LessThan {
        val token: String = "less_than"

        final case class Proposition(label: String, compareTo: Long)
            extends quivr.Proposition
            with quivr.Operations.LessThan

        final case class Proof(transactionBind: TxBind)
            extends quivr.Proof(token, transactionBind)
            with quivr.Operations.LessThan
      }

      object GreaterThan {
        val token: String = "greater_than"

        final case class Proposition(label: String, compareTo: Long)
            extends quivr.Proposition
            with quivr.Operations.GreaterThan

        final case class Proof(transactionBind: TxBind)
            extends quivr.Proof(token, transactionBind)
            with quivr.Operations.GreaterThan
      }

      object EqualTo {
        val token: String = "equal_to"

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
        val token: String = "threshold"

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
        val token: String = "not"

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
        val token: String = "and"

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
        val token: String = "or"

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

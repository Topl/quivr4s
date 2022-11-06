package co.topl

package object quivr {

  // Propositions represent challenges that must be satisfied
  sealed abstract class Proposition

  // For each Proposition there is a corresponding Proof that can be constructed to satisfy the given Proposition
  sealed abstract class Proof(bindToTransaction: Array[Byte])

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

  object Models {

    object Primitive {

      object Locked {
        val token: Byte = 0: Byte

        final case class Proposition(data: Array[Byte]) extends quivr.Proposition with quivr.Operations.Locked

        final case class Proof(transactionBind: Array[Byte]) extends quivr.Proof(transactionBind) with quivr.Operations.Locked
      }

      object Digest {
        val token: Byte = 1: Byte

        final case class Proposition(digest: Array[Byte]) extends quivr.Proposition with quivr.Operations.Digest

        final case class Proof(preimage: Array[Byte], transactionBind: Array[Byte])
          extends quivr.Proof(transactionBind)
            with quivr.Operations.Digest
      }

      object DigitalSignature {
        val token: Byte = 2: Byte

        final case class Proposition(vk: Array[Byte]) extends quivr.Proposition with quivr.Operations.DigitalSignature

        final case class Proof(transactionBind: Array[Byte])
          extends quivr.Proof(transactionBind)
            with quivr.Operations.DigitalSignature
      }
    }

    object Contextual {

      object Height {
        val token: Byte = -1: Byte

        final case class Proposition(min: Long, max: Long) extends quivr.Proposition with quivr.Operations.HeightRange

        final case class Proof(transactionBind: Array[Byte])
          extends quivr.Proof(transactionBind)
            with quivr.Operations.HeightRange
      }

      object Slot {
        val token: Byte = -2: Byte

        final case class Proposition(min: Long, max: Long) extends quivr.Proposition with quivr.Operations.HeightRange

        final case class Proof(transactionBind: Array[Byte])
          extends quivr.Proof(transactionBind)
            with quivr.Operations.HeightRange
      }

      object ExactMatch {
        val token: Byte = -3: Byte
      }

    }

    object Compositional {

      object Not {
        val token: Byte = 126: Byte

        final case class Proposition(proposition: quivr.Proposition) extends quivr.Proposition with quivr.Operations.Not

        final case class Proof(transactionBind: Array[Byte]) extends quivr.Proof(transactionBind) with quivr.Operations.Not
      }

      object Threshold {
        sealed abstract class BooleanOp

        case object And extends BooleanOp

        case object Or extends BooleanOp

        val token: Byte = 127: Byte

        final case class Proposition(
                                challenges: Array[quivr.Proposition],
                                threshold: Int,
                                connector: BooleanOp
                              ) extends quivr.Proposition
          with quivr.Operations.Threshold

        final case class Proof(
                          responses: Array[Option[quivr.Proof]],
                          transactionBind: Array[Byte]
                        ) extends quivr.Proof(transactionBind)
          with quivr.Operations.Threshold
      }
    }

  }
}



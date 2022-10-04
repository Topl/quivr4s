package co.topl

package object quivr {
  type Digest = Array[Byte]

  case class Box(evidence: Array[Byte], value: Box.Value)

  object Box {
    case class Id(bytes: Array[Byte])
    case class Datum(data: Option[Metadata])

    sealed abstract class Value
    object Values {
      case class Lvl(quantity: Int, datum: Box.Datum) extends Value
      case class Topl(quantity: Int, datum: Box.Datum) extends Value
    }
  }

  case class SecretKey(bytes: Array[Byte])
  case class VerificationKey(bytes: Array[Byte])
  case class KeyPair(sk: SecretKey, vk: VerificationKey)
  case class Signature(bytes: Array[Byte])

  case class Accumulator()

  case class TypedEvidence(prefix: Array[Byte], bytes: Array[Byte])

  case class Metadata(prefix: Array[Byte], value: Array[Byte])

  case class Contract(propositions: Set[Proposal[_]], threshold: Int)

  case class Attestation(proofs: Set[Proof[_]])

  sealed abstract class HasEval[E] {
    val eval: E
  }

  trait Proposal[E] extends HasEval[E]
  trait Proof[E] extends HasEval[E]
  trait Verification[E] extends HasEval[E]
  trait Signatory[E] extends HasEval[E]

  trait IoTransaction[I[_], O[_]] {
    val inputs: List[I]
    val outputs: List[O]
  }

  trait SpentTransactionOutput[V <: Box.Value] {
    val value: V
    val utxoReference: Box.Id
    val contract: Contract
    val attestation: Attestation
  }

  trait UnspentTransactionOutput[V <: Box.Value] {
    val value: V
    val spendEvidence: TypedEvidence
  }
}

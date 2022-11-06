package co.topl

import co.topl.crypto.hash.blake2b256
import co.topl.quivr.Evaluation.{Datum, IncludesHeight}

object Tetra {

  case class IoTx(
    inputs:   List[IoTx.SpentOutput],
    outputs:  List[IoTx.UnspentOutput],
    schedule: IoTx.Schedule,
    metadata: Option[Array[Byte]]
  )

  object IoTx {
    case class Schedule(min: Long, max: Long, timestamp: Long)

    case class SpentOutput(reference: Box.Id, attestation: Attestation, value: Box.Value, datum: Datums.SpentOutput)

    case class UnspentOutput(address: Address, value: Box.Value, datum: Datums.UnspentOutput)
  }

  case class Address(network: Int, ledger: Int, evidence: Predicate.Id)

  case class Box(image: Predicate.Image, value: Box.Value, datum: Option[Datums.Box])

  object Box {
    case class Id(bytes: Array[Byte])

    sealed abstract class Value

    object Values {
      case class Token(quantity: Long) extends Box.Value

      case class Asset(label: Byte, quantity: Long, metadata: Array[Byte]) extends Box.Value
    }
  }

  case class Predicate(conditions: List[quivr.Proposition], threshold: Int)

  object Predicate {

    case class Image(
      digest:    Array[Byte],
      threshold: Int
    ) // serialize Image and commit bits via hash, call this commit the Id

    case class Id(image: Image)

    def idFromImage(image: Predicate.Image): Predicate.Id = Id(
      Predicate.Image(blake2b256.hash(image.digest).value, image.threshold)
    )
  }

  case class Attestation(predicateImage: Predicate.Image, value: List[Option[(quivr.Proposition, quivr.Proof)]])

  object Datums {
    case class Eon(beginSlot: Long, height: Long) extends Datum with IncludesHeight

    case class Era(beginSlot: Long, height: Long) extends Datum with IncludesHeight

    case class Epoch(beginSlot: Long, height: Long) extends Datum with IncludesHeight

    case class Header(height: Long) extends Datum with IncludesHeight

    case class Body(root: Array[Byte]) extends Datum

    case class IoTx(signableBytes: Array[Byte]) extends Datum

    case class Box(metadata: Array[Byte]) extends Datum

    case class SpentOutput(metadata: Array[Byte]) extends Datum

    case class UnspentOutput(metadata: Array[Byte]) extends Datum
  }
}

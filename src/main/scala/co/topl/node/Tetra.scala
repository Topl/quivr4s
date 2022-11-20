package co.topl.node

import co.topl.node.Models.{Metadata, Root, SignableBytes}
import co.topl.quivr

object Tetra {
  case class IoTx(
    inputs:   List[IoTx.SpentOutput],
                   outputs:  List[IoTx.UnspentOutput],
                   schedule: IoTx.Schedule,
                   datum: Metadata
                 )

  object IoTx {
    case class Schedule(min: Long, max: Long, timestamp: Long)

    case class SpentOutput(reference: Box.Id, attestation: Attestation, value: Box.Value, datum: Datums.SpentOutput)

    case class UnspentOutput(address: Address, value: Box.Value, datum: Datums.UnspentOutput)

    def signableBytes(tranasction: IoTx): SignableBytes = ???

    
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
    case class Id(bytes: Array[Byte])

    case class Known(conditions: List[Option[quivr.Proposition]])

    // use a Root here so we can provide a membership proof of
    case class Image(root: Root, threshold: Int)

    def idFromImage(image: Predicate.Image): Predicate.Id = ???
    //  Id(Predicate.Image(blake2b256.hash(image.root).value, image.threshold)
  }

  case class Attestation(image: Predicate.Image, known: Predicate.Known, responses: List[Option[quivr.Proof]])

  object Attestation {
    val default: List[Option[quivr.Proof]] = List(Some(quivr.Models.Primitive.Locked.Proof()))
  }

  object Datums {
    case class Eon(beginSlot: Long, height: Long) extends Datum with IncludesHeight

    case class Era(beginSlot: Long, height: Long) extends Datum with IncludesHeight

    case class Epoch(beginSlot: Long, height: Long) extends Datum with IncludesHeight

    case class Header(height: Long, metadata: Metadata) extends Datum with IncludesHeight

    case class Body(root: Root, metadata: Metadata) extends Datum

    case class IoTx(metadata: Metadata) extends Datum

    case class Box(metadata: Metadata) extends Datum

    case class SpentOutput(metadata: Metadata) extends Datum

    case class UnspentOutput(metadata: Metadata) extends Datum
  }
}

package co.topl.node

import co.topl.node.Models.{Blobs, Metadata, BigData, SmallData, Root}
import co.topl.quivr
import co.topl.quivr.runtime.{Datum, IncludesHeight}

object Tetra {

  case class IoTransaction(
    inputs:  List[IoTransaction.SpentOutput],
    outputs: List[IoTransaction.UnspentOutput],
    datum:   Datums.IoTx,
    bigData: Option[BigData]
  )

  object IoTransaction {
    case class Schedule(min: Long, max: Long, timestamp: Long)

    case class SpentOutput(reference: Box.Id, attestation: Attestation, value: Box.Value, datum: Datums.SpentOutput)

    case class UnspentOutput(address: Address, value: Box.Value, datum: Datums.UnspentOutput)

    def signableBytes(tranasction: IoTransaction): SignableBytes = ???

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

    case class Header(height: Long, metadata: SmallData) extends Datum with IncludesHeight

    case class Body(root: Root, metadata: SmallData) extends Datum

    case class IoTx(schedule: IoTransaction.Schedule, blobId: Blobs.Id, metadata: SmallData) extends Datum

    case class Box(metadata: SmallData) extends Datum

    case class SpentOutput(metadata: SmallData) extends Datum

    case class UnspentOutput(metadata: SmallData) extends Datum
  }


  trait Blob {
    val value: Array[Byte]
    val signableBytes: Array[Byte]
    val id: Blobs.Id
  }

  object Blobs {
    case class Id(value: Array[Byte])
    case class IoTx(value: BigData)
  }

  type Root = Array[Byte]
  type SmallData = Array[Byte]
  type BigData = Option[Blob]
  type IdentifiableBytes = Array[Byte] // hash(SignableBytes)
}

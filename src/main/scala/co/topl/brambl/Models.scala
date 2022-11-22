package co.topl.brambl

import co.topl.node.Models.{Metadata, SignableBytes}
import co.topl.node.Tetra

object Models {
  trait Signable {
    def getSignableBytes: SignableBytes = ???
  }

  case class Indices(x: Int, y: Int, z: Int)

  case class UnprovenSpentOutputV1(
                                  reference: Tetra.Box.Id,
                                  knownPredicate: Tetra.Predicate.Known,
                                  value: Tetra.Box.Value,
                                  datum: Tetra.Datums.SpentOutput
                                )

  case class UnprovenSpentOutputV2(
                                  reference: Tetra.Box.Id,
                                  value: Tetra.Box.Value,
                                  datum: Tetra.Datums.SpentOutput
                                )

  case class UnprovenIoTx[Input](inputs:   List[Input],
                                 outputs:  List[Tetra.IoTx.UnspentOutput],
                                 schedule: Tetra.IoTx.Schedule,
                                 metadata: Metadata)


  // Adding additional functionality to tetra models

  // Abstract away how the signable bytes are generated
  implicit class SignableBytesFromIoTx(iotx: Tetra.IoTx) extends Signable
  implicit class SignableBytesFromUnprovenIoTx[T](iotx: UnprovenIoTx[T]) extends Signable

  // Abstract away how an address is generated from a predicate image
  implicit class AddressFromPredicateImage(predicateImage: Tetra.Predicate.Image) {
    def generateAddress: Tetra.Address = Tetra.Address(0, 0, Tetra.Predicate.idFromImage(predicateImage))
  }

  // Abstract away how a predicate image is generated from a predicate
  implicit class ImageFromPredicate(predicate: Tetra.Predicate) {
    def image: Tetra.Predicate.Image  = Tetra.Predicate.Image(???, predicate.threshold)
  }

  implicit def intFromBoolean(b: Boolean): Int = if(b) 1 else 0
}

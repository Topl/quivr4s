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

  case class UnprovenIoTxV1(inputs:   List[UnprovenSpentOutputV1],
                          outputs:  List[Tetra.IoTx.UnspentOutput],
                          schedule: Tetra.IoTx.Schedule,
                          metadata: Metadata
                         )

  case class UnprovenIoTxV2(inputs:   List[UnprovenSpentOutputV2],
                          outputs:  List[Tetra.IoTx.UnspentOutput],
                          schedule: Tetra.IoTx.Schedule,
                          metadata: Metadata
                         )

  // Adding additional functionality to tetra models

  // Abstract away how the signable bytes are generated
  implicit class SignableBytesFromIoTx(iotx: Tetra.IoTx) extends Signable
  implicit class SignableBytesFromUnprovenIoTxV1(iotx: UnprovenIoTxV1) extends Signable
  implicit class SignableBytesFromUnprovenIoTxV2(iotx: UnprovenIoTxV2) extends Signable

  // Abstract away how an address is generated from a predicate image
  implicit class AddressFromPredicateImage(predicateImage: Tetra.Predicate.Image) {
    def generateAddress: Tetra.Address = Tetra.Address(0, 0, Tetra.Predicate.idFromImage(predicateImage))
  }

  // Abstract away how a predicate image is generated from a predicate
  implicit class ImageFromPredicate(predicate: Tetra.Predicate) {
    def image: Tetra.Predicate.Image  = Tetra.Predicate.Image(???, predicate.threshold)
  }

}

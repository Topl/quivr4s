package co.topl.brambl
//
//import co.topl.node.TetraDatums
//
object Models {
  case class Indices(x: Int, y: Int, z: Int)
//
//  case class UnprovenIoTx[Input](inputs:   List[Input],
//                                 outputs:  List[TetraDatums.IoTx.UnspentOutput],
//                                 schedule: TetraDatums.IoTx.Schedule,
//                                 metadata: Metadata)
//
//
//  // Adding additional functionality to tetra models
//
//  // Abstract away how the signable bytes are generated
//  implicit class SignableBytesFromIoTx(iotx: TetraDatums.IoTx) extends Signable
//  implicit class SignableBytesFromUnprovenIoTx[T](iotx: UnprovenIoTx[T]) extends Signable
//
//  // Abstract away how an address is generated from a predicate image
//  implicit class AddressFromPredicateImage(predicateImage: TetraDatums.Predicate.Image) {
//    def generateAddress: TetraDatums.Address = TetraDatums.Address(0, 0, TetraDatums.Predicate.idFromImage(predicateImage))
//  }
//
//  // Abstract away how a predicate image is generated from a predicate
//  implicit class ImageFromPredicate(predicate: TetraDatums.Predicate) {
//    def image: TetraDatums.Predicate.Image  = TetraDatums.Predicate.Image(???, predicate.threshold)
//  }
//
//  implicit def intFromBoolean(b: Boolean): Int = if(b) 1 else 0
}

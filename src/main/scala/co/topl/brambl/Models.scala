package co.topl.brambl

import co.topl.quivr.SignableBytes
import co.topl.node.Tetra

object Models {
  // Ideally should be defined at a higher level than brambl
  trait Signable {
    def getSignableBytes: SignableBytes = ???
  }

  case class Indices(x: Int, y: Int, z: Int)

  case class UnprovenSpentOutputV1(
                                  reference: Tetra.Box.Id,
                                  knownPredicate: Tetra.Predicate.Known,
                                  value: Tetra.Box.Value,
                                  datum: Tetra.Datums.SpentOutput
                                ) extends Signable

  case class UnprovenSpentOutputV2(
                                  reference: Tetra.Box.Id,
                                  value: Tetra.Box.Value,
                                  datum: Tetra.Datums.SpentOutput
                                ) extends Signable

  case class UnprovenIoTransaction[Input <: Signable](inputs:   List[Input],
                                 outputs:  List[Tetra.IoTransaction.UnspentOutput],
                                 datum: Tetra.Datums.IoTx,
                                 metadata: Option[Tetra.Blob]) extends Signable {
    override def getSignableBytes: SignableBytes = {
      val inputsSignable = inputs.flatMap(_.getSignableBytes).toArray
      val outputsSignable = outputs.flatMap(_.getSignableBytes).toArray
      val datumSignable = datum.getSignableBytes
      val metaSignable = if(metadata.isEmpty) Array() else metadata.get.signableBytes
      inputsSignable ++ outputsSignable ++ datumSignable ++ metaSignable
    }
  }


  // Adding additional functionality to tetra models

  // Abstract away how the signable bytes are generated from a transaction
  implicit class SignableBytesFromIoTx(iotx: Tetra.IoTransaction) extends Signable
  // Abstract away how the signable bytes are generated from an unspent output
  implicit class SignableBytesFromUnspentOutput(output: Tetra.IoTransaction.UnspentOutput) extends Signable
  // Abstract away how the signable bytes are generated from a Datums.Iotx
  implicit class SignableBytesFromIoTxDatum(datum: Tetra.Datums.IoTx) extends Signable

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

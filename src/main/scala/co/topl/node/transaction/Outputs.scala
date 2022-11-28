package co.topl.node.transaction

import co.topl.node.Reference
import co.topl.node.box.{Blob, Lock, Value}
import co.topl.quivr.runtime.Datum

sealed abstract class Output {
  val value: Value
  val datum: Datum
}

object Outputs {

  case class Spent(
    reference:   Reference,
    attestation: Attestation,
    value:       Value,
    datum:       Datums.SpentOutput,
    locksOpt:    List[Option[Lock]] = List()
  ) extends Output

  case class Unspent(
    address:  Address,
    value:    Value,
    datum:    Datums.UnspentOutput,
    blobsOpt: List[Option[Blob]] = List()
  ) extends Output
}

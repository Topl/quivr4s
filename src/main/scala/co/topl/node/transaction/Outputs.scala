package co.topl.node.transaction

import co.topl.node.Reference
import co.topl.node.box.{Blob, Lock, Value}

sealed abstract class Output {
  val value: Value
}

object Outputs {

  case class Spent(
    reference:   Reference,
    attestation: Attestation,
    value:       Value,
    datum:       Datums.SpentOutput,
    locksOpt:    List[Option[Lock]]
  ) extends Output

  case class Unspent(
    address:  Address,
    value:    Value,
    datum:    Datums.UnspentOutput,
    blobsOpt: List[Option[Blob]]
  ) extends Output
}

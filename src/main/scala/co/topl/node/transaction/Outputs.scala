package co.topl.node.transaction

import co.topl.node.box.{Blob, Lock, Value}
import co.topl.node.{Address, Reference}
import co.topl.quivr.runtime.Datum

sealed abstract class Output[O] {
  val value: Value
  val datum: Datum
  val opts: List[Option[O]]
}

object Outputs {

  case class Spent(
    reference:   Reference,
    attestation: Attestation,
    value:       Value,
    datum:       Datums.SpentOutput,
    opts:        List[Option[Lock]]
  ) extends Output[Lock]

  case class Unspent(
    address: Address,
    value:   Value,
    datum:   Datums.UnspentOutput,
    opts:    List[Option[Blob]]
  ) extends Output[Blob]
}

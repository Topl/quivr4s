package co.topl.node.transaction

import co.topl.node.{Address, Events}
import co.topl.node.box.{Blob, Value}
import co.topl.quivr.runtime.Datum

case class UnspentOutput(
  address: Address,
  value:   Value,
  datum:   Datum[Events.UnspentOutput],
  opts:    List[Option[Blob]]
) extends Spendable[Value, Blob]

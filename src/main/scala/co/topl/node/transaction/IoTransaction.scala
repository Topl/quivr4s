package co.topl.node.transaction

import co.topl.node.Events
import co.topl.quivr.runtime.Datum

case class IoTransaction(
  inputs:  List[SpentOutput],
  outputs: List[UnspentOutput],
  datum:   Datum[Events.IoTransaction],
  opts:    List[Option[Spendable[_, _]]]
) extends Spendable[(List[SpentOutput], List[UnspentOutput]), Spendable[_, _]] {
  override val value: (List[SpentOutput], List[UnspentOutput]) = (inputs, outputs)
}

object IoTransaction {
  case class Schedule(min: Long, max: Long, timestamp: Long)
}

package co.topl.node.transaction

import co.topl.node.Events
import co.topl.quivr.runtime.Datum

case class IoTransaction(
  inputs:  List[SpentTransactionOutput],
  outputs: List[UnspentTransactionOutput],
  datum:   Datum[Events.IoTransaction]
)

object IoTransaction {
  case class Schedule(min: Long, max: Long, timestamp: Long)
}

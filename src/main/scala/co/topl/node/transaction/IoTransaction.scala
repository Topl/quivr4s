package co.topl.node.transaction

case class IoTransaction(
  inputs:     List[Outputs.Spent],
  outputs:    List[Outputs.Unspent],
  datum:      Datums.IoTx,
  outputsOpt: List[Option[Output]]
)

object IoTransaction {
  case class Schedule(min: Long, max: Long, timestamp: Long)
}

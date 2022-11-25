package co.topl.node.transaction

import co.topl.node.TetraDatums

case class IoTransaction(
  inputs:  List[SpentOutput],
  outputs: List[UnspentOutput],
  datum:   TetraDatums.IoTx,
  blobOpt: Option[Blob]
)

object IoTransaction {
  case class Schedule(min: Long, max: Long, timestamp: Long)
}

package co.topl.node.outputs

import co.topl.node.Tetra.Datums

case class IoTransaction(
  inputs:  List[SpentOutput],
  outputs: List[UnspentOutput],
  datum:   Datums.IoTx,
  blobOpt: Option[Blob]
)

object IoTransaction {
  case class Id(bytes: Array[Byte])
  case class Schedule(min: Long, max: Long, timestamp: Long)
}

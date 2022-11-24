package co.topl.node.transaction

import co.topl.node.TetraDatums.Datums
import co.topl.node.{Address, TetraDatums}

case class UnspentOutput(
  address: Address,
  value:   Box.Value,
  datum:   TetraDatums.UnspentOutput,
  blobOpt: Option[Blob]
)

object UnspentOutput {
  case class Id(bytes: Array[Byte])
}

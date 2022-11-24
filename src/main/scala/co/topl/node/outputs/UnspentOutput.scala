package co.topl.node.outputs

import co.topl.node.Tetra.Datums
import co.topl.node.Address

case class UnspentOutput(
  address: Address,
  value:   Box.Value,
  datum:   Datums.UnspentOutput,
  blobOpt: Option[Blob]
)

object UnspentOutput {
  case class Id(bytes: Array[Byte])
}

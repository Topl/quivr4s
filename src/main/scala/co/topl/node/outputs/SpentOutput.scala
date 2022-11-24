package co.topl.node.outputs

import co.topl.node.Tetra.Datums
import co.topl.node.Attestation

case class SpentOutput(
  reference:   Box.Id,
  attestation: Attestation,
  value:       Box.Value,
  datum:       Datums.SpentOutput,
  blobOpt:     Option[Blob]
)

object SpentOutput {
  case class Id(bytes: Array[Byte])
}

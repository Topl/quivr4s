package co.topl.node.transaction

import co.topl.node.{Attestation, TetraDatums}

case class SpentOutput(
  reference:   Box.Id,
  attestation: Attestation,
  value:       Box.Value,
  datum:       TetraDatums.SpentOutput,
  blobOpt:     Option[Blob]
)

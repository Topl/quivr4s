package co.topl.node.transaction

import co.topl.node.{Attestation, Identifiers, TetraDatums}

case class SpentOutput(
  reference:   Identifiers.Box, // this is a Box id... is that an Identifiers.Box?
  attestation: Attestation,
  value:       Box.Value,
  datum:       TetraDatums.SpentOutput,
  blobOpt:     Option[Blob]
)

package co.topl.node.transaction

import co.topl.node.{Address, TetraDatums}

case class UnspentOutput(
  address: Address,
  value:   Box.Value,
  datum:   TetraDatums.UnspentOutput,
  blobOpt: Option[Blob]
)

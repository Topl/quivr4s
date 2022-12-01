package co.topl.node.transaction

import co.topl.node.box.{Lock, Value}
import co.topl.node.{Events, KnownIdentifier}
import co.topl.quivr.runtime.Datum

case class SpentTransactionOutput(
  reference:   KnownIdentifier,
  attestation: Attestation,
  value:       Value,
  datum:       Datum[Events.SpentOutput],
  opts:        List[Option[Lock]]
) extends TransactionOutput
  with Reference[Lock]

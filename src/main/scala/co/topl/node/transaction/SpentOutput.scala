package co.topl.node.transaction

import co.topl.node.{Events, Reference}
import co.topl.node.box.{Lock, Value}
import co.topl.quivr.runtime.Datum

case class SpentOutput(
  reference:   Reference,
  attestation: Attestation,
  value:       Value,
  datum:       Datum[Events.SpentOutput],
  opts:        List[Option[Lock]]
) extends Spendable[Value, Lock]

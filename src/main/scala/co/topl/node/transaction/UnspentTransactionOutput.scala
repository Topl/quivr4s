package co.topl.node.transaction

import co.topl.node.box.{Blob, Value}
import co.topl.node.{Address, Events}
import co.topl.quivr.runtime.Datum

case class UnspentTransactionOutput(
  address: Address,
  value:   Value,
  datum:   Datum[Events.UnspentOutput],
  opts:    List[Option[Blob]]
) extends TransactionOutput
    with Reference[Blob]

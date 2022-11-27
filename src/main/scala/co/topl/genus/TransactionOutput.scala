package co.topl.genus

import co.topl.node.box.Box
import co.topl.node.transaction.Address

case class TransactionOutput(
  id:       Box.Id,
  boxValue: Box.Value,
  address:  Address,
  state:    OutputState = OutputStates.Unspent
)

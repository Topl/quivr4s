package co.topl.genus

import co.topl.node.Address
import co.topl.node.transaction.Box

case class TransactionOutput(
  id:       Box.Id,
  boxValue: Box.Value,
  address:  Address,
  state:    OutputState = OutputStates.Unspent
)

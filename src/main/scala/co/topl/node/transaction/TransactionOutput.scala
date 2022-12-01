package co.topl.node.transaction

import co.topl.node.box.Value

trait TransactionOutput {
  val value: Value
}

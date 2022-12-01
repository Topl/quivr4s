package co.topl.node.transaction

import co.topl.node.box.Value
import co.topl.node.Event
import co.topl.quivr.runtime.Datum

trait TransactionOutput[O] extends Reference[O] {
  val value: Value
  val datum: Datum[Event]
  val opts: List[Option[O]]
}

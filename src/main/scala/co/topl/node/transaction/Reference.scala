package co.topl.node.transaction

import co.topl.node.Event
import co.topl.quivr.runtime.Datum

trait Reference[O] {
  val datum: Datum[Event]
  val opts: List[Option[O]]
}

package co.topl.node.transaction

import co.topl.node.Event
import co.topl.quivr.runtime.Datum

trait Spendable[V, O] {
  val value: V
  val datum: Datum[_ <: Event]
  val opts: List[Option[O]]
}

package co.topl.node.transaction

import co.topl.quivr.runtime.Datum

trait Spendable[V, O] {
  val value: V
  val datum: Datum[_]
  val opts: List[Option[O]]
}

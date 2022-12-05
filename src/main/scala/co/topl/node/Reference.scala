package co.topl.node

import co.topl.quivr.runtime.Datum

trait Reference[O] {
  val datum: Datum[Event]
  val opts: List[Option[O]]
}



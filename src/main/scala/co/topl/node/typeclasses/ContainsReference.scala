package co.topl.node.typeclasses

import co.topl.node.Reference
import co.topl.node.box.Value
import co.topl.quivr.runtime.Datum

trait ContainsReference {
  val value: Value
  val datum: Datum[_]
  val opts: List[Option[Reference[_]]]
}


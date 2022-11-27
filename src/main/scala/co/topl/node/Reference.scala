package co.topl.node

sealed abstract class Reference {
  val index: Int
  val id: Identifier
}

object References {
  case class KnownPredicate(index: Int, id: Identifiers.BoxLock) extends Reference
  case class Blob(index: Int, id: Identifiers.BoxValue) extends Reference
  case class Output(index: Int, id: Identifiers.IoTransaction) extends Reference
}

package co.topl.node

sealed abstract class Reference {
  val index: Int
  val id: Identifier
}

object References {
  case class KnownPredicate32(index: Int, id: Identifiers.BoxLock32) extends Reference
  case class KnownPredicate64(index: Int, id: Identifiers.BoxLock64) extends Reference

  case class Blob32(index: Int, id: Identifiers.BoxValue32) extends Reference
  case class Blob64(index: Int, id: Identifiers.BoxValue64) extends Reference

  case class Output32(index: Int, id: Identifiers.IoTransaction32) extends Reference
  case class Output64(index: Int, id: Identifiers.IoTransaction64) extends Reference

}

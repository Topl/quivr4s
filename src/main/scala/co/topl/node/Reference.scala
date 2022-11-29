package co.topl.node

// References combine an Identifier and an index
// todo: discuss whether the index here should be a 128-bit to allow for embedding Cartesian indices (3x 0 -> 2^31)
sealed abstract class Reference {
  val network: Int
  val ledger: Int
  val indices: List[Int]
  val id: Identifier
}

object References {

  case class KnownPredicate32(network: Int, ledger: Int, indices: List[Int], id: Identifiers.BoxLock32)
      extends Reference

  case class KnownPredicate64(network: Int, ledger: Int, indices: List[Int], id: Identifiers.BoxLock64)
      extends Reference

  case class Blob32(network: Int, ledger: Int, indices: List[Int], id: Identifiers.BoxValue32) extends Reference
  case class Blob64(network: Int, ledger: Int, indices: List[Int], id: Identifiers.BoxValue64) extends Reference

  case class Output32(network: Int, ledger: Int, indices: List[Int], id: Identifiers.IoTransaction32) extends Reference
  case class Output64(network: Int, ledger: Int, indices: List[Int], id: Identifiers.IoTransaction64) extends Reference
}

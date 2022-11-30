package co.topl.node

// References combine an Identifier and an index
sealed abstract class Reference {
  val network: Int
  val ledger: Int
  val indices: List[Int]
  val id: Identifier
}

object References {

  case class KnownLeaf32(network: Int, ledger: Int, indices: List[Int], id: Identifiers.AccumulatorRoot32)
      extends Reference

  case class KnownLeaf64(network: Int, ledger: Int, indices: List[Int], id: Identifiers.AccumulatorRoot64)
      extends Reference

  case class KnownPredicate32(network: Int, ledger: Int, indices: List[Int], id: Identifiers.Lock32) extends Reference

  case class KnownPredicate64(network: Int, ledger: Int, indices: List[Int], id: Identifiers.Lock64) extends Reference

  case class KnownBlob32(network: Int, ledger: Int, indices: List[Int], id: Identifiers.BoxValue32) extends Reference

  case class KnownBlob64(network: Int, ledger: Int, indices: List[Int], id: Identifiers.BoxValue64) extends Reference

  case class KnownSpendable32(network: Int, ledger: Int, indices: List[Int], id: Identifiers.IoTransaction32)
      extends Reference

  case class KnownSpendable64(network: Int, ledger: Int, indices: List[Int], id: Identifiers.IoTransaction64)
      extends Reference
}

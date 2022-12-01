package co.topl.node

/**
 * Reference combine an Identifier and an index. They are meant to refer to a specific element (or elements)
 * of a certain type (denoted by the Identifier) available at a specific location (network + ledger)
 */
sealed abstract class KnownIdentifier {
  val network: Int
  val ledger: Int
  val indices: List[Int]
  val id: Identifier
}

object Known {

  case class Leaf32(network: Int, ledger: Int, indices: List[Int], id: Identifiers.AccumulatorRoot32)
      extends KnownIdentifier

  case class Leaf64(network: Int, ledger: Int, indices: List[Int], id: Identifiers.AccumulatorRoot64)
      extends KnownIdentifier

  case class Predicate32(network: Int, ledger: Int, indices: List[Int], id: Identifiers.Lock32) extends KnownIdentifier

  case class Predicate64(network: Int, ledger: Int, indices: List[Int], id: Identifiers.Lock64) extends KnownIdentifier

  case class Blob32(network: Int, ledger: Int, indices: List[Int], id: Identifiers.BoxValue32) extends KnownIdentifier

  case class Blob64(network: Int, ledger: Int, indices: List[Int], id: Identifiers.BoxValue64) extends KnownIdentifier

  case class Reference32(network: Int, ledger: Int, indices: List[Int], id: Identifiers.IoTransaction32)
      extends KnownIdentifier

  case class Reference64(network: Int, ledger: Int, indices: List[Int], id: Identifiers.IoTransaction64)
      extends KnownIdentifier
}

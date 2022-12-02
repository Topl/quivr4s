package co.topl.node

/**
 * Reference combine an Identifier and an index. They are meant to refer to a specific element (or elements)
 * of a certain type (denoted by the Identifier) available at a specific location (network + ledger)
 */
sealed abstract class KnownIdentifier {
  val network: Int
  val ledger: Int
  val index: Int
  val id: Identifier
}

object KnownIdentifiers {

  case class TransactionOutput32(network: Int, ledger: Int, index: Int, id: Identifiers.IoTransaction32)
    extends KnownIdentifier

  case class TransactionOutput64(network: Int, ledger: Int, index: Int, id: Identifiers.IoTransaction64)
    extends KnownIdentifier

}

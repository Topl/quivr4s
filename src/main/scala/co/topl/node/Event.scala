package co.topl.node

/**
 * Events are uniquely identifiable occurrences of state mutations within the blockchain protocol.
 * Each event is associated with certain data that may be updated every tick.
 */
sealed abstract class Event

object Events {
  case class Eon(beginSlot: Long, height: Long) extends Event
  case class Era(beginSlot: Long, height: Long) extends Event
  case class Epoch(beginSlot: Long, height: Long) extends Event
  case class Header(height: Long) extends Event

  case class IoTransaction(
    schedule:     co.topl.node.transaction.IoTransaction.Schedule,
    references32: List[KnownIdentifiers.TransactionOutput32],
    references64: List[KnownIdentifiers.TransactionOutput64],
    metadata:     SmallData
  ) extends Event

  case class SpentTransactionOutput(
    references: List[KnownReferences.Predicate],
    metadata:   SmallData
  ) extends Event

  case class UnspentTransactionOutput(
    references: List[KnownReferences.Blob],
    metadata:   SmallData
  ) extends Event

  case class Root(value: co.topl.node.Root) extends Event
}

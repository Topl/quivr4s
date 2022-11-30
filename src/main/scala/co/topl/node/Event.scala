package co.topl.node

sealed abstract class Event

object Events {
  case class Eon(beginSlot: Long, height: Long) extends Event
  case class Era(beginSlot: Long, height: Long) extends Event
  case class Epoch(beginSlot: Long, height: Long) extends Event
  case class Header(height: Long) extends Event
  case class Body(root: Root) extends Event

  case class IoTransaction(
    schedule:     co.topl.node.transaction.IoTransaction.Schedule,
    references32: List[References.KnownSpendable32],
    references64: List[References.KnownSpendable64],
    metadata:     SmallData
  ) extends Event

  case class SpentOutput(
    references32: List[References.KnownPredicate32],
    references64: List[References.KnownPredicate64],
    metadata:     SmallData
  ) extends Event

  case class UnspentOutput(
    references32: List[References.KnownBlob32],
    references64: List[References.KnownBlob64],
    metadata:     SmallData
  ) extends Event
}

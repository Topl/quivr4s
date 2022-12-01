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
    references32: List[Known.Reference32],
    references64: List[Known.Reference64],
    metadata:     SmallData
  ) extends Event

  case class SpentOutput(
    references32: List[Known.Predicate32],
    references64: List[Known.Predicate64],
    metadata:     SmallData
  ) extends Event

  case class UnspentOutput(
    references32: List[Known.Blob32],
    references64: List[Known.Blob64],
    metadata:     SmallData
  ) extends Event
}

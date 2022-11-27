package co.topl.node.transaction

import co.topl.node.{References, Root, SmallData}
import co.topl.quivr.runtime.{Datum, IncludesHeight}

object Datums {
    case class Eon(beginSlot: Long, height: Long) extends Datum with IncludesHeight

    case class Era(beginSlot: Long, height: Long) extends Datum with IncludesHeight

    case class Epoch(beginSlot: Long, height: Long) extends Datum with IncludesHeight

    case class Header(height: Long) extends Datum with IncludesHeight

    case class Body(root: Root, metadata: SmallData) extends Datum

    case class IoTx(schedule: IoTransaction.Schedule, references: List[References.Output32], metadata: SmallData) extends Datum

    case class SpentOutput(references: List[References.KnownPredicate32], metadata: SmallData) extends Datum

    case class UnspentOutput(references: List[References.Blob32], metadata: SmallData) extends Datum
  }

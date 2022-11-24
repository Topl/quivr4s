package co.topl.node

import co.topl.node.transaction.{Blob, IoTransaction}
import co.topl.quivr.runtime.{Datum, IncludesHeight}

object TetraDatums {
    case class Eon(beginSlot: Long, height: Long) extends Datum with IncludesHeight

    case class Era(beginSlot: Long, height: Long) extends Datum with IncludesHeight

    case class Epoch(beginSlot: Long, height: Long) extends Datum with IncludesHeight

    case class Header(height: Long) extends Datum with IncludesHeight

    case class Body(root: Root, metadata: SmallData) extends Datum

    case class IoTx(schedule: IoTransaction.Schedule, blobId: Blob.Id, metadata: SmallData) extends Datum

    case class SpentOutput(blobId: Blob.Id, metadata: SmallData) extends Datum

    case class UnspentOutput(blobId: Blob.Id, metadata: SmallData) extends Datum
  }

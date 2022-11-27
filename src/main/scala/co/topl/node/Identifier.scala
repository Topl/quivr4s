package co.topl.node

import co.topl.node.box.{Lock, Value}
import co.topl.node.typeclasses.ContainsEvidence.Ops
import co.topl.node.typeclasses.ContainsSignable.instances._

sealed abstract class Identifier {
  val tag: String
  val value: Array[Byte]
}

object Identifiers {
  case class BoxLock(value: Array[Byte], tag: String = "box_lock") extends Identifier   // roots known predicates
  case class BoxValue(value: Array[Byte], tag: String = "box_value") extends Identifier // roots known blobs
  case class IoTransaction(value: Array[Byte], tag: String = "iotx") extends Identifier // roots known outputs
  // Ledger roots known transactions
  // Body roots known Ledgers
  // Header roots known Body
  // Epoch roots known Headers
  // Era roots known Epochs
  // Eon roots known Eras

  def boxLock(lock: Lock): Identifiers.BoxLock =
    Identifiers.BoxLock(lock.evidence.value)

  def boxValue(boxValue: Value): Identifiers.BoxValue =
    Identifiers.BoxValue(boxValue.evidence.value)

  def transaction(transaction: co.topl.node.transaction.IoTransaction): Identifiers.IoTransaction =
    Identifiers.IoTransaction(transaction.evidence.value)
}

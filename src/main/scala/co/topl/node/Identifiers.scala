package co.topl.node

import co.topl.node.typeclasses.ContainsEvidence.Ops
import co.topl.node.typeclasses.ContainsSignable.instances._

sealed abstract class Identifier(tag: Byte) {
  val bytes: Array[Byte]
}

object Identifiers {
  case class Lock(bytes: Array[Byte]) extends Identifier(0: Byte) // roots known predicates
  case class Box(bytes: Array[Byte]) extends Identifier(1: Byte) // roots known blobs
  case class IoTransaction(bytes: Array[Byte]) extends Identifier(2: Byte) // roots known outputs
  // Ledger roots known transactions
  // Body roots known Ledgers
  // Header roots known Body
  // Epoch roots known Headers
  // Era roots known Epochs
  // Eon roots known Eras

  def lock(lock: co.topl.node.Lock): Identifiers.Lock =
    Identifiers.Lock(lock.evidence.bytes)

  def box(box: co.topl.node.transaction.Box): Identifiers.Box =
    Identifiers.Box(box.evidence.bytes)

  def transaction(transaction: co.topl.node.transaction.IoTransaction): Identifiers.IoTransaction =
    Identifiers.IoTransaction(transaction.evidence.bytes)
}

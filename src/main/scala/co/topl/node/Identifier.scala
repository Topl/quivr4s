package co.topl.node

import co.topl.crypto.hash.digest.{Digest32, Digest64}
import co.topl.node.box.{Lock, Value}
import co.topl.node.transaction.IoTransaction
import co.topl.node.typeclasses.ContainsEvidence.{ListOps, SignableOps}
import co.topl.node.typeclasses.ContainsSignable.instances._
import co.topl.node.typeclasses.{ContainsEvidence, ContainsSignable}

// Identifiers are tagged evidence
sealed abstract class Identifier {
  val tag: String
  val evidence: Evidence[_]
  val value: Array[Byte] = evidence.value
}

object Identifiers {

  // known leaves
  case class AccumulatorRoot32(evidence: Evidence[Digest32], tag: String = "acc_root_32") extends Identifier
  case class AccumulatorRoot64(evidence: Evidence[Digest64], tag: String = "acc_root_64") extends Identifier

  // known predicates
  case class BoxLock32(evidence: Evidence[Digest32], tag: String = "box_lock_32") extends Identifier
  case class BoxLock64(evidence: Evidence[Digest64], tag: String = "box_lock_64") extends Identifier

  // known blobs
  case class BoxValue32(evidence: Evidence[Digest32], tag: String = "box_value_32") extends Identifier
  case class BoxValue64(evidence: Evidence[Digest64], tag: String = "box_value_64") extends Identifier

  // known outputs
  case class IoTransaction32(evidence: Evidence[Digest32], tag: String = "iotx_32") extends Identifier
  case class IoTransaction64(evidence: Evidence[Digest64], tag: String = "iotx_64") extends Identifier

  // Ledger roots known transactions
  // Body roots known Ledgers
  // Header roots known Body
  // Epoch roots known Headers
  // Era roots known Epochs
  // Eon roots known Eras

  def evidenceList32[T: ContainsSignable](list: List[T])(implicit
    ev:                                         ContainsEvidence[List[T]]
  ): Identifiers.AccumulatorRoot32 =
    Identifiers.AccumulatorRoot32(list.merkleEvidence.sized32Evidence)

  def evidenceList64[T: ContainsSignable](list: List[T])(implicit
    ev:                                         ContainsEvidence[List[T]]
  ): Identifiers.AccumulatorRoot64 =
    Identifiers.AccumulatorRoot64(list.merkleEvidence.sized64Evidence)

  def boxLock32(lock: Lock): Identifiers.BoxLock32 =
    Identifiers.BoxLock32(lock.blake2bEvidence.sized32Evidence)

  def boxLock64(lock: Lock): Identifiers.BoxLock64 =
    Identifiers.BoxLock64(lock.blake2bEvidence.sized64Evidence)

  def boxValue32(value: Value): Identifiers.BoxValue32 =
    Identifiers.BoxValue32(value.blake2bEvidence.sized32Evidence)

  def boxValue64(value: Value): Identifiers.BoxValue64 =
    Identifiers.BoxValue64(value.blake2bEvidence.sized64Evidence)

  def transaction32(transaction: IoTransaction): Identifiers.IoTransaction32 =
    Identifiers.IoTransaction32(transaction.blake2bEvidence.sized32Evidence)

  def transaction64(transaction: IoTransaction): Identifiers.IoTransaction64 =
    Identifiers.IoTransaction64(transaction.blake2bEvidence.sized64Evidence)
}

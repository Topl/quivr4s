package co.topl.node.typeclasses

import co.topl.node.{Identifiers, Reference, References}
import co.topl.node.box.{Lock, Value}
import co.topl.node.transaction.IoTransaction

trait ContainsReference[T] {
  def pointer(t: T): Reference
}

object ContainsReference {
  def apply[T](t: T)(implicit ev: ContainsReference[T]): ContainsReference[T] = ev

  def fromBoxLock32(network: Int, ledger: Int, indices: List[Int], lock: Lock): References.KnownPredicate32 =
    References.KnownPredicate32(network, ledger, indices, Identifiers.boxLock32(lock))

  def fromBoxLock64(network: Int, ledger: Int, indices: List[Int], lock: Lock): References.KnownPredicate64 =
    References.KnownPredicate64(network, ledger, indices, Identifiers.boxLock64(lock))

  def fromBoxValue32(network: Int, ledger: Int, indices: List[Int], value: Value): References.KnownBlob32 =
    References.KnownBlob32(network, ledger, indices, Identifiers.boxValue32(value))

  def fromBoxValue64(network: Int, ledger: Int, indices: List[Int], value: Value): References.KnownBlob64 =
    References.KnownBlob64(network, ledger, indices, Identifiers.boxValue64(value))

  def fromTransaction32(network: Int, ledger: Int, indices: List[Int], iotx: IoTransaction): References.KnownSpendable32 =
    References.KnownSpendable32(network, ledger, indices, Identifiers.transaction32(iotx))

  def fromTransaction64(network: Int, ledger: Int, indices: List[Int], iotx: IoTransaction): References.KnownSpendable64 =
    References.KnownSpendable64(network, ledger, indices, Identifiers.transaction64(iotx))
}

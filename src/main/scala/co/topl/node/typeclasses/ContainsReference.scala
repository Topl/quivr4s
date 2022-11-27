package co.topl.node.typeclasses

import co.topl.node.{Identifiers, Reference, References}
import co.topl.node.box.Lock
import co.topl.node.transaction.IoTransaction

trait ContainsReference[T] {
  def pointer(t: T): Reference
}

object ContainsReference {
  def apply[T](t: T)(implicit ev: ContainsReference[T]): ContainsReference[T] = ev

  def fromBoxLock(index: Int, bl: Lock): References.KnownPredicate =
    References.KnownPredicate(index, Identifiers.boxLock(bl))

  def fromBoxValue(index: Int, bv: Box.Value): References.Blob =
    References.Blob(index, Identifiers.boxValue(bv))

  def fromTransaction(index: Int, iotx: IoTransaction): References.Output =
    References.Output(index, Identifiers.transaction(iotx))
}

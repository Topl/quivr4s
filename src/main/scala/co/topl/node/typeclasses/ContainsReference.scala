package co.topl.node.typeclasses

import co.topl.node.{Identifiers, Reference, References}
import co.topl.node.box.{Lock, Value}
import co.topl.node.transaction.IoTransaction

trait ContainsReference[T] {
  def pointer(t: T): Reference
}

object ContainsReference {
  def apply[T](t: T)(implicit ev: ContainsReference[T]): ContainsReference[T] = ev

  def fromBoxLock32(index: Int, bl: Lock): References.KnownPredicate32 =
    References.KnownPredicate32(index, Identifiers.boxLock32(bl))

  def fromBoxLock64(index: Int, bl: Lock): References.KnownPredicate64 =
    References.KnownPredicate64(index, Identifiers.boxLock64(bl))

  def fromBoxValue32(index: Int, bv: Value): References.Blob32 =
    References.Blob32(index, Identifiers.boxValue32(bv))

  def fromBoxValue64(index: Int, bv: Value): References.Blob64 =
    References.Blob64(index, Identifiers.boxValue64(bv))

  def fromTransaction32(index: Int, iotx: IoTransaction): References.Output32 =
    References.Output32(index, Identifiers.transaction32(iotx))

  def fromTransaction64(index: Int, iotx: IoTransaction): References.Output64 =
    References.Output64(index, Identifiers.transaction64(iotx))
}

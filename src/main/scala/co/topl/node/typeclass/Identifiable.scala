package co.topl.node.typeclass

import co.topl.crypto.hash.blake2b256

trait EventId {
  val bytes: Array[Byte]
}

trait Identifiable[T] {
  def id(t: T): EventId
}

object Identifiable {
  def apply[T](t: T)(implicit ev: Identifiable[T]): Identifiable[T] = ev

  def fromSignable[T](implicit sb: ContainsSignable[T]): Identifiable[T] = (t: T) =>
    new EventId {
      override val bytes: Array[Byte] = blake2b256.hash(sb.signableBytes(t)).value
  }
}

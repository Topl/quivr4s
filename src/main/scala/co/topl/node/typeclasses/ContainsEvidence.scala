package co.topl.node.typeclasses

import co.topl.crypto.hash.blake2b256

trait Evidence {
  val bytes: Array[Byte]
}

object Identifiers {
  case class Predicate(bytes: Array[Byte]) extends Evidence
  case class Blob(bytes: Array[Byte]) extends Evidence
  case class Box(bytes: Array[Byte]) extends Evidence
  case class IoTransaction(bytes: Array[Byte]) extends Evidence
}

trait ContainsEvidence[T] {
  def evidenceOf(t: T): Evidence
}

object ContainsEvidence {
  def apply[T](t: T)(implicit ev: ContainsEvidence[T]): ContainsEvidence[T] = ev

  def fromSignable[T](implicit sb: ContainsSignable[T]): ContainsEvidence[T] = (t: T) =>
    new Evidence {
      override val bytes: Array[Byte] = blake2b256.hash(sb.signableBytes(t)).value
  }

  implicit class Ops[T: ContainsSignable](t: T) {
    def id: Evidence = fromSignable.evidenceOf(t)
  }
}
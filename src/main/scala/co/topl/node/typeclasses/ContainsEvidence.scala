package co.topl.node.typeclasses

import co.topl.crypto.hash.blake2b256

// evidence is a unique
trait Evidence {
  val bytes: Array[Byte]
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
    def evidence: Evidence = fromSignable.evidenceOf(t)
  }
}

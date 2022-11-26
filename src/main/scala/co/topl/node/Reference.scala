package co.topl.node

import co.topl.crypto.hash.blake2b256
import co.topl.node.typeclasses.ContainsEvidence.Ops
import co.topl.node.typeclasses.{ContainsRoot, ContainsSignable}
import co.topl.node.typeclasses.ContainsSignable.instances._

trait Reference {
  val index: Int
  val id: Identifier
}

trait ContainsReference[T] {
  def pointer(t: T): Reference
}

object ContainsReference {
  def apply[T](t: T)(implicit ev: ContainsReference[T]): ContainsReference[T] = ev

  def fromRoot[T](implicit root: ContainsRoot[T]): ContainsReference[T] = (t: T) =>
    new Reference {
      override val bytes: Array[Byte] = blake2b256.hash(root.rootOf(t)).value
    }

  implicit class Ops[T: ContainsRoot](t: T) {
    def reference: Reference = fromRoot.evidenceOf(t)
  }
}

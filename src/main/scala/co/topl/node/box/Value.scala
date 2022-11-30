package co.topl.node.box

sealed abstract class Value {
  val quantity: Long
  val blobs: List[Option[Blob]] = List()
}

object Values {
  case class Token(quantity: Long) extends Value

  case class Asset(label: String, quantity: Long, metadata: Array[Byte]) extends Value
}
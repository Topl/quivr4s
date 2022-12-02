package co.topl.node.box

import co.topl.node.SmallData

sealed abstract class Value {
  val quantity: Long
  val blobs: List[Option[Blob]]
}

object Values {
  case class Token(quantity: Long, blobs: List[Option[Blob]]) extends Value

  case class Asset(label: String, quantity: Long, metadata: SmallData, blobs: List[Option[Blob]]) extends Value
}
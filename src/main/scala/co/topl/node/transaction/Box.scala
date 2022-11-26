package co.topl.node.transaction

import co.topl.node.{Lock, Root}

case class Box(lock: Lock, value: Box.Value)

object Box {


  sealed abstract class Value {
    val quantity: Long
    val blobsRoot: List[Option[Blob]] = List()
  }

  object Values {
    case class Token(quantity: Long) extends Box.Value

    case class Asset(label: Byte, quantity: Long, metadata: Array[Byte]) extends Box.Value
  }
}

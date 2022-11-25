package co.topl.node.transaction

import co.topl.node.Predicate

case class Box(image: Predicate.Commitment, value: Box.Value)

object Box {
  case class Id(bytes: Array[Byte])

  sealed abstract class Value(quantity: Long)

  object Values {
    case class Token(quantity: Long) extends Box.Value(quantity)

    case class Asset(label: Byte, quantity: Long, metadata: Array[Byte]) extends Box.Value(quantity)
  }
}

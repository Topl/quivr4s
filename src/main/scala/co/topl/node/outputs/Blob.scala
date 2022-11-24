package co.topl.node.outputs

trait Blob {
  val value: Array[Byte] // may be up to 15kB
}

object Blob {
  case class Id(value: Array[Byte])
}

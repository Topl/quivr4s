package co.topl.node.transaction

trait Blob {
  val value: Array[Byte] // may be up to 15kB
}

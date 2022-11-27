package co.topl.node.box

trait Blob {
  val value: Array[Byte] // may be up to 15kB
}

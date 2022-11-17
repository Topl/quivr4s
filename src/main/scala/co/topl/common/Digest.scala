package co.topl.common

trait Reveal
trait Commit

  object Digests {
    case class Digest(bytes: Array[Byte]) extends Digest
    case class Preimage(value: Array[Byte], salt: Long) extends Preimage
  }

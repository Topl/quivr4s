package co.topl.node

import co.topl.crypto.hash.digest.{Digest, Digest32, Digest64}
import co.topl.crypto.implicits._

// evidence should be a unique set of bytes
sealed abstract class Evidence[+D: Digest](val value: Array[Byte]) {
  val digest: D
}

object Evidence {
  case class Sized32(digest: Digest32) extends Evidence[Digest32](digest.value)
  case class Sized64(digest: Digest64) extends Evidence[Digest64](digest.value)
}

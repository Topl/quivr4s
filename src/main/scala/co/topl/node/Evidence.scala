package co.topl.node

import co.topl.crypto.hash.digest.{Digest, Digest32, Digest64}
import co.topl.crypto.implicits._

// evidence should be a unique set of bytes

/**
 * Evidence should be a succinct, unique set of bytes used to distinguish between any two data objects.
 * Evidence is also meant to have a minimal amount of structure such that other domains may provide
 * their own similarly unique & succinct values. Quivr can cast such external domain "evidence" into Topl evidence
 * through the use of ContainsSignable[Evidence[_]]
 * @param value the primary bytes that serves as the unique Evidence
 * @tparam D a fixed sized digest for labeling the expected size of the evidence value
 */
sealed abstract class Evidence[+D: Digest](val value: Array[Byte]) {
  val digest: D
}

object Evidence {
  case class Sized32(digest: Digest32) extends Evidence[Digest32](digest.value)
  case class Sized64(digest: Digest64) extends Evidence[Digest64](digest.value)
}

package co.topl.quivr

import org.bouncycastle.crypto.digests.Blake2bDigest

package object api {

  /**
   * Compute the Blake2b-256 hash of the input
   * @param in array of any length
   * @return array of length=32
   */
  def blake2b256Hash(in: Array[Byte]): Array[Byte] = {
    val blake2b256 = new Blake2bDigest(256)
    blake2b256.update(in, 0, in.length)
    val out = new Array[Byte](32)
    blake2b256.doFinal(out, 0)
    out
  }
}

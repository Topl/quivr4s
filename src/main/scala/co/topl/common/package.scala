package co.topl

package object common {
    type Preimage = Array[Byte]
    type Digest = Array[Byte]

  case class DigestVerification(digest: Digest, preimage: Preimage, salt: Long)

type SecretKey = Array[Byte]
  type VerificationKey = Array[Byte]
  type Witness = Array[Byte]

    case class SignatureVerification(vk: VerificationKey)
}

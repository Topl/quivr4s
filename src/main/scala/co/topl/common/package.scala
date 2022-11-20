package co.topl

package object common {

    case class Preimage(input: Array[Byte], salt: Array[Byte])
    case class Digest(value: Array[Byte])

  case class DigestVerification(digest: Digest, preimage: Preimage, salt: Long)

case class Message(value: Array[Byte])
  case class VerificationKey(value: Array[Byte])
  case class Witness(value: Array[Byte])

    case class SignatureVerification(vk: VerificationKey, sig: Witness, msg: Message)
}

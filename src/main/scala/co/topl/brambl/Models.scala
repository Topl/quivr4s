package co.topl.brambl

import quivr.models.VerificationKey

object Models {
  case class SigningKey(value: Array[Byte]) // To mirror VerificationKey
  case class KeyPair(sk: SigningKey, vk: VerificationKey)
  case class Indices(x: Int, y: Int, z: Int)
}

package co.topl.brambl

import co.topl.common.Models.VerificationKey


object Models {
  case class SigningKey(value: Array[Byte]) // To mirror VerificationKey
  case class KeyPair(sk: SigningKey, vk: VerificationKey)
  case class Indices(x: Int, y: Int, z: Int)
}

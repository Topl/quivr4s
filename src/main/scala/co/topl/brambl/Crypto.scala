package co.topl.brambl


import co.topl.brambl.Models.{KeyPair, SigningKey}
import co.topl.common.Models.{SignatureVerification, VerificationKey, Witness}
import co.topl.crypto.hash.blake2b256
import co.topl.crypto.signatures
import co.topl.crypto.{PrivateKey, PublicKey}


// Brambl wrapper for common crypto calls
// Allows conversion between topl.crypto types and quivr4s types

object Crypto {
  object Blake2b256 {
    def hash(msg: Array[Byte]): Array[Byte] = blake2b256.hash(msg).value
  }

  object Curve25519 {
    def getKeyPair(seed: Array[Byte]): KeyPair = {
      val (sk, vk) = signatures.Curve25519.createKeyPair(seed)
      KeyPair(SigningKey(sk.value), VerificationKey(vk.value))
    }

    def sign(sk: SigningKey, msg: Array[Byte]): Witness =
      Witness(signatures.Curve25519.sign(PrivateKey(sk.value), msg).value)

    def verify(v: SignatureVerification): Boolean =
      signatures.Curve25519.verify(
        signatures.Signature(v.sig.value),
        v.msg.value,
        PublicKey(v.vk.value))
  }


}

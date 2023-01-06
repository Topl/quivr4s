package co.topl.brambl.routines.signatures

import co.topl.brambl.Models.{KeyPair, SigningKey}
import co.topl.crypto.{PrivateKey, PublicKey, signatures}
import co.topl.quivr.algebras.SignatureVerifier
import co.topl.quivr.runtime.{QuivrRuntimeError, QuivrRuntimeErrors}
import com.google.protobuf.ByteString
import quivr.models.{SignableBytes, SignatureVerification, VerificationKey, Witness}

object Curve25519Signature extends Signing {
  override val routine: String = "curve25519"

  override def createKeyPair(seed: Array[Byte]): KeyPair = {
    val (sk, vk) = signatures.Curve25519.createKeyPair(seed)
    val skBytes = sk.value
    val vkBytes = vk.value
    KeyPair(SigningKey(skBytes), VerificationKey(ByteString.copyFrom(vkBytes)))
  }

  override def sign(sk: SigningKey, msg: SignableBytes): Witness =
    Witness(ByteString.copyFrom(signatures.Curve25519.sign(PrivateKey(sk.value), msg.value.toByteArray).value))
}

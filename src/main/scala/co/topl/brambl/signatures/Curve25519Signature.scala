package co.topl.brambl.signatures

import co.topl.brambl.Models.{KeyPair, SigningKey}
import co.topl.crypto.{signatures, PrivateKey, PublicKey}
import co.topl.quivr.algebras.SignatureVerifier
import co.topl.quivr.runtime.{QuivrRuntimeError, QuivrRuntimeErrors}
import com.google.protobuf.ByteString
import quivr.models.SignableBytes
import quivr.models.SignatureVerification
import quivr.models.VerificationKey
import quivr.models.Witness

object Curve25519Signature extends SignatureVerifier[Option] with Signing {
  override val routine: String = "curve25519"

  override def createKeyPair(seed: Array[Byte]): KeyPair = {
    val (sk, vk) = signatures.Curve25519.createKeyPair(seed)
    val skBytes = sk.value
    val vkBytes = vk.value
    KeyPair(SigningKey(skBytes), VerificationKey(ByteString.copyFrom(vkBytes)))
  }

  override def sign(sk: SigningKey, msg: SignableBytes): Witness =
    Witness(ByteString.copyFrom(signatures.Curve25519.sign(PrivateKey(sk.value), msg.value.toByteArray).value))

  override def validate(v: SignatureVerification): Option[Either[QuivrRuntimeError, SignatureVerification]] =
    Some(
      if (
        signatures.Curve25519.verify(
          signatures.Signature(v.signature.get.value.toByteArray),
          v.message.get.value.toByteArray,
          PublicKey(v.verificationKey.get.value.toByteArray)
        )
      ) Right(v)
      else Left(QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable)
    )
}

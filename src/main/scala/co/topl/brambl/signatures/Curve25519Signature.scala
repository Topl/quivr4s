package co.topl.brambl.signatures

import co.topl.brambl.Models.{KeyPair, SigningKey}
import co.topl.common.Models.{SignatureVerification, VerificationKey, Witness}
import co.topl.crypto.{PrivateKey, PublicKey, signatures}
import co.topl.quivr.SignableBytes
import co.topl.quivr.algebras.SignatureVerifier
import co.topl.quivr.runtime.{QuivrRuntimeError, QuivrRuntimeErrors}

object Curve25519Signature extends SignatureVerifier[Option] with Signing {
  override val routine: String = "curve25519"

  override def createKeyPair(seed: Array[Byte]): KeyPair = {
    val (sk, vk) = signatures.Curve25519.createKeyPair(seed)
    KeyPair(SigningKey(sk.value), VerificationKey(vk.value))
  }

  override def sign(sk: SigningKey, msg: SignableBytes): Witness =
    Witness(signatures.Curve25519.sign(PrivateKey(sk.value), msg).value)

  override def validate(v: SignatureVerification): Option[Either[QuivrRuntimeError, SignatureVerification]] =
    Some(
      if(
        signatures.Curve25519.verify(signatures.Signature(v.sig.value), v.msg.value, PublicKey(v.vk.value))
      ) Right(v)
      else Left(QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable)
    )
}
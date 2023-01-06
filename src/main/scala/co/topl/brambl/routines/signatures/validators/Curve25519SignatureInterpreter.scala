package co.topl.brambl.routines.signatures.validators

import co.topl.brambl.routines.Routine
import co.topl.crypto.{PublicKey, signatures}
import co.topl.quivr.algebras.SignatureVerifier
import co.topl.quivr.runtime.{QuivrRuntimeError, QuivrRuntimeErrors}
import quivr.models.SignatureVerification

object Curve25519SignatureInterpreter extends SignatureVerifier[Option] with Routine {
  override val routine: String = "curve25519"

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

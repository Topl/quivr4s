package co.topl.brambl.routines.digests.validators

import co.topl.brambl.routines.Routine
import co.topl.crypto.hash.blake2b256
import co.topl.quivr.algebras.DigestVerifier
import co.topl.quivr.runtime.{QuivrRuntimeError, QuivrRuntimeErrors}
import com.google.protobuf.ByteString
import quivr.models.{Digest, DigestVerification, Preimage}

object Blake2b256DigestInterpreter extends DigestVerifier[Option] with Routine {
  override val routine: String = "blake2b256"

  override def validate(v: DigestVerification): Option[Either[QuivrRuntimeError, DigestVerification]] = {
    val test = blake2b256.hash(v.preimage.get.input.toByteArray ++ v.preimage.get.salt.toByteArray).value
    Some(
      if (v.digest.get.value.digest32.get.value.toByteArray.sameElements(test)) Right(v)
      else Left(QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable)
    )
  }
}

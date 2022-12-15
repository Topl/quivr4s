package co.topl.brambl.digests

import co.topl.common.Models.{Digest, DigestVerification, Preimage}
import co.topl.crypto.hash.blake2b256
import co.topl.quivr.algebras.DigestVerifier
import co.topl.quivr.runtime.{QuivrRuntimeError, QuivrRuntimeErrors}

object Blake2b256Digest extends DigestVerifier[Option] with Hash {
  override val routine: String = "blake2b256"

  override def hash(preimage: Preimage): Digest = Digest(blake2b256.hash(preimage.input ++ preimage.salt).value)

  override def validate(v: DigestVerification): Option[Either[QuivrRuntimeError, DigestVerification]] = {
    val test = blake2b256.hash(v.preimage.input ++ v.preimage.salt).value
    Some(
      if(
        v.digest.value.sameElements(test)
      ) Right(v)
      else Left(QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable)
    )
  }
}

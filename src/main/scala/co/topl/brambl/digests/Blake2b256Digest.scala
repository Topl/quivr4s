package co.topl.brambl.digests

import co.topl.crypto.hash.blake2b256
import co.topl.quivr.algebras.DigestVerifier
import co.topl.quivr.runtime.{QuivrRuntimeError, QuivrRuntimeErrors}
import com.google.protobuf.ByteString
import quivr.models.Digest
import quivr.models.DigestVerification
import quivr.models.Preimage

object Blake2b256Digest extends DigestVerifier[Option] with Hash {
  override val routine: String = "blake2b256"

  override def hash(preimage: Preimage): Digest = Digest().withDigest32(
    Digest.Digest32(ByteString.copyFrom(blake2b256.hash(preimage.input.toByteArray ++ preimage.salt.toByteArray).value))
  )

  override def validate(v: DigestVerification): Option[Either[QuivrRuntimeError, DigestVerification]] = {
    val test = blake2b256.hash(v.preimage.get.input.toByteArray ++ v.preimage.get.salt.toByteArray).value
    Some(
      if (v.digest.get.value.digest32.get.value.toByteArray.sameElements(test)) Right(v)
      else Left(QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable)
    )
  }
}

package co.topl.brambl.routines.digests

import co.topl.crypto.hash.blake2b256
import co.topl.quivr.algebras.DigestVerifier
import co.topl.quivr.runtime.{QuivrRuntimeError, QuivrRuntimeErrors}
import com.google.protobuf.ByteString
import quivr.models.{Digest, DigestVerification, Preimage}

object Blake2b256Digest extends Hash {
  override val routine: String = "blake2b256"

  override def hash(preimage: Preimage): Digest = Digest().withDigest32(
    Digest.Digest32(ByteString.copyFrom(blake2b256.hash(preimage.input.toByteArray ++ preimage.salt.toByteArray).value))
  )
}

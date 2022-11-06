package co.topl.quivr.archive.v1

import cats._
import cats.data.OptionT
import cats.implicits._
import co.topl.crypto.hash.blake2b256

object Verification {

  def digest[F[_]: Applicative](
      proposition: Propositions.Digest,
      proof: Proofs.Digest
  ): F[Boolean] =
    (blake2b256.hash(proof.preimage).value sameElements proposition.digest)
      .pure[F]

}

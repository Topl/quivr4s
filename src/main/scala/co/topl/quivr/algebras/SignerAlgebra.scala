package co.topl.quivr.algebras

import co.topl.quivr._

trait SignerAlgebra[F[_]] {
  def create(seed: Array[Byte]): F[KeyPair]

  def sign(sk: SecretKey, msg: Array[Byte]): F[Signature]

  def verify(vk: VerificationKey, msg: Array[Byte], sig: Signature): F[Boolean]
}





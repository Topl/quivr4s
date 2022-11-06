package co.topl.quivr.archive.interpreters

import co.topl.quivr.archive.algebras.SignerAlgebra
import co.topl.quivr.archive.{KeyPair, SecretKey, Signatory, Signature, VerificationKey}

object Signers {
  case class Create(seed: Array[Byte]) extends Signatory[KeyPair] {
    val eval: KeyPair = signingAlgebra.create(seed).eval
  }

  case class Sign(eval: Signature) extends Signatory[Signature]

  case class Verify(vk: VerificationKey, msg: Array[Byte], sig: Signature) extends Signatory[Boolean] {
    val eval: Boolean = signingAlgebra.verify(vk, msg, sig).eval
  }

  implicit val signingAlgebra: SignerAlgebra[Signatory] = new SignerAlgebra[Signatory] {
    override def create(seed: Array[Byte]): Signatory[KeyPair] = ???

    override def sign(sk: SecretKey, msg: Array[Byte]): Signatory[Signature] = ???

    override def verify(vk: VerificationKey, msg: Array[Byte], sig: Signature): Signatory[Boolean] = ???
  }
}

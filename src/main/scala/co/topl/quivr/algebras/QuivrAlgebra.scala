package co.topl.quivr.algebras

import co.topl.quivr
import co.topl.quivr.{Proof, Proposal, Signature, VerificationKey}

sealed abstract class QuivrAlgebra[F[_]] {
  // primitive types
  def bool(boolean: Boolean): F[Boolean]
  def num(int: Int): F[Int]

  // operations
  def or(left: F[Boolean], right: F[Boolean]): F[Boolean]
  def and(left: F[Boolean], right: F[Boolean]): F[Boolean]
  def not(boolean: F[Boolean]): F[Boolean]

  def equalTo(left: F[Int], right: F[Int]): F[Boolean]
  def greaterThan(left: F[Int], right: F[Int]): F[Boolean]
  def lessThan(left: F[Int], right: F[Int]): F[Boolean]

  def sum(left: F[Int], right: F[Int]): F[Int]
}

trait ProposerAlgebra[F[_]] extends QuivrAlgebra[F] {
  def signature(vk: VerificationKey): F[VerificationKey]
}

trait ProverAlgebra[F[_]] extends QuivrAlgebra[F] {
  def signature(sk: quivr.SecretKey, msg: Array[Byte]): F[quivr.Signature]
}

trait VerifierAlgebra[F[_]] extends QuivrAlgebra[F] {
  def signature(vk: Proposal[VerificationKey], msg: Array[Byte], sig: Proof[Signature]): F[Boolean]
}

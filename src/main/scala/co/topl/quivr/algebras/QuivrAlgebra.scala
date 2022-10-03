package co.topl.quivr.algebras

import co.topl.quivr
import co.topl.quivr.{Proof, Proposal, Signature, VerificationKey}

trait BooleanAlgebra[F[_]] {
  // primitive type
  def bool(boolean: Boolean): F[Boolean]

  // operations
  def or(left: F[Boolean], right: F[Boolean]): F[Boolean]
  def and(left: F[Boolean], right: F[Boolean]): F[Boolean]
  def not(boolean: F[Boolean]): F[Boolean]
}

trait IntAlgebra[F[_]] {
  def num(int: Int): F[Int]
  def sum(left: F[Int], right: F[Int]): F[Int]
  def equalTo(left: F[Int], right: F[Int]): F[Boolean]
  def greaterThan(left: F[Int], right: F[Int]): F[Boolean]
  def lessThan(left: F[Int], right: F[Int]): F[Boolean]
}

trait DigestAlgebra[F[_]] {
  def hash(digest: Array[Byte]): F[Array[Byte]]
  def equal(left: F[Array[Byte]], right: F[Array[Byte]]): F[Boolean]
  def reveal(preimage: F[Array[Byte]], digest: F[Array[Byte]]): F[Boolean]
}

trait AccumulatorAlgebra[F[_]] {
  def root(digest: Array[Byte]): F[Array[Byte]]
  def leaf(index:Int, digest: Array[Byte]): F[Array[Byte]]
  def leafReveal(preimage: F[Array[Byte]], digest: F[Array[Byte]]): F[Boolean]
  def includedIn(root: F[Array[Byte]], leaves: List[F[Array[Byte]]]): F[List[Option[_]]]
}

sealed abstract class QuivrAlgebra[F[_]] extends BooleanAlgebra[F] with IntAlgebra[F] with DigestAlgebra[F] with AccumulatorAlgebra[F]

trait ProposerAlgebra[F[_]] extends QuivrAlgebra[F] {
  def signature(vk: VerificationKey): F[VerificationKey]
}

trait ProverAlgebra[F[_]] extends QuivrAlgebra[F] {
  def signature(sk: quivr.SecretKey, msg: Array[Byte]): F[quivr.Signature]
}

trait VerifierAlgebra[F[_]] extends QuivrAlgebra[F] {
  def signature(vk: Proposal[VerificationKey], msg: Array[Byte], sig: Proof[Signature]): F[Boolean]
}

package co.topl.quivr.archive.algebras

import co.topl.quivr.archive.{SecretKey, Signature, VerificationKey}

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

abstract class QuivrAlgebra[F[_]] extends BooleanAlgebra[F] with IntAlgebra[F]

trait ProposerAlgebra[F[_]] extends QuivrAlgebra[F] {
  def signature(vk: VerificationKey): F[VerificationKey]
//  def digest(digest: Digest): F[Digest]
//  def accumulator(root: Digest): F[Digest]
}

trait ProverAlgebra[F[_]] extends QuivrAlgebra[F] {
  def signature(sk: SecretKey): F[Signature]
//  def digest(preimage: Array[Byte]): F[Digest]
//  def accumulator(tree: quivr.Accumulator, leaf: Digest): F[List[Digest]]
}

// trait VerifierAlgebra[F[_]] extends QuivrAlgebra[F] {
//   def signature(
//       vk: Proposal[VerificationKey],
//       sig: Proof[Signature]
//   )(implicit msg: Array[Byte]): F[Boolean]
// //  def digest(left: F[Digest], right: F[Digest]): F[Boolean]
// //  def merkle(vk: Proposal[VerificationKey], leaf: F[Digest], witness: Proof[List[Digest]]): F[Boolean]
// }

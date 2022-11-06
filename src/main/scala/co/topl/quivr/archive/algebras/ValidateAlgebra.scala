package co.topl.quivr.archive.algebras

trait ValidateAlgebra[F[_]] {
  def validate(value: F[_]): F[Boolean]
}

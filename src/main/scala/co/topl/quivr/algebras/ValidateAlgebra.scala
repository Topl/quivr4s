package co.topl.quivr.algebras

trait ValidateAlgebra[F[_]] {
  def validate(value: F): Boolean
}

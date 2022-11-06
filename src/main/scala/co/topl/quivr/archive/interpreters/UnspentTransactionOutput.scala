package co.topl.quivr.archive.interpreters

import co.topl.quivr.archive.{Metadata, TypedEvidence, UnspentTransactionOutput}

case class UTxO[V](
    spendEvidence: TypedEvidence,
    value: V,
    datum: Option[UnspentTransactionOutput.Datum]
) extends UnspentTransactionOutput[V]

object UnspentTransactionOutput {
  case class Datum(data: Option[Metadata])

  trait SomeContext // this would provide the env for computation, inputs would need to be available here for some checks

  // val lvlValidation: SomeContext => Boolean = ???

  // this will need to apply the constraints for specific values
  // implicit def utxoValidate(implicit context: SomeContext): ValidateAlgebra[UnspentTransactionOutput[_]] = ???

}

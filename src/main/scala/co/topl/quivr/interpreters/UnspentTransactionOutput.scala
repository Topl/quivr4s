package co.topl.quivr.interpreters

import co.topl.quivr.algebras.ValidateAlgebra
import co.topl.quivr.{Box, Metadata, TypedEvidence, UnspentTransactionOutput}

case class UTxO[V <: Box.Value](spendEvidence: TypedEvidence,
                                value: V,
                                datum: Option[UnspentTransactionOutput.Datum],
                               ) extends UnspentTransactionOutput[V]

object UnspentTransactionOutput {
  case class Datum(data: Option[Metadata])

  trait SomeContext //this would provide the env for computation, inputs would need to be available here for some checks

  //val lvlValidation: SomeContext => Boolean = ???

  // this will need to apply the constraints for specific values
 // implicit def utxoValidate(implicit context: SomeContext): ValidateAlgebra[UnspentTransactionOutput[_]] = ???

}

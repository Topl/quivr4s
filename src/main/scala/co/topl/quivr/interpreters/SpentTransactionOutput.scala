package co.topl.quivr.interpreters

import co.topl.quivr.algebras.ValidateAlgebra
import co.topl.quivr.{Attestation, Box, Contract, Metadata, SpentTransactionOutput}

case class STxO[V <: Box.Value](utxoReference: Box.Id,
                                contract: Contract,
                                attestation: Attestation,
                                value: V,
                                datum: Option[SpentTransactionOutput.Datum]
                               ) extends SpentTransactionOutput[V]

object SpentTransactionOutput {
  case class Datum(data: Option[Metadata])


 // implicit val spendValidation: ValidateAlgebra[SpentTransactionOutput[_]] = ???
  // check that the typed evidence of the contract matches the evidence on the utxoReference
  // check that the contract is satisfied by the provided attestation

  //  (stxo: STxO[_]) => {
  //    stxo.contract.propositions.zip(stxo.attestation.proofs).foldLeft(0) {
  //      case (countValidProofs, (currentProposal, currentProof)) =>
  //
  //        verify(currentExposure.proposition, currentExposure.proof) match {
  //          case Some(value) =>
  //          case None => countValidProofs
  //        }
  //        if (verify(currentExposure.proposition, currentExposure.proof)) countValidProofs + 1
  //        else countValidProofs
  //    }
  //
  //    //=> verify(c.proposition, c.proof) }
  //    //overallResult <- if (leafResults.challenges.size >= )
  //    result <-
  //  } yield ???
}

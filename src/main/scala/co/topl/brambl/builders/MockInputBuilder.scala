package co.topl.brambl.builders

import cats.implicits.catsSyntaxOptionId
import co.topl.brambl.builders.Models.InputBuildRequest
import co.topl.brambl.models.transaction.SpentTransactionOutput
import co.topl.brambl.wallet.MockStorage


object MockInputBuilder extends InputBuilder {
  override def constructUnprovenInput(data: InputBuildRequest): Either[BuilderError, SpentTransactionOutput] = {
    val id = MockStorage.getKnownIdentifierByIndices(data.idx)
    val box = id.flatMap(MockStorage.getBoxByKnownIdentifier)
    val attestation = box.flatMap(_.lock).map(MockAttestationBuilder.constructUnprovenAttestation)
    val value = box.flatMap(_.value)
    val datum = data.datum
    val opts = List()
    (id, attestation, value) match {
      case (Some(knownId), Some(Right(att)), Some(boxVal)) =>
        Right(SpentTransactionOutput(knownId.some, att.some, boxVal.some, datum, opts))
      case (_, Some(Left(err)), _) => Left(err)
      case _ =>
        Left(BuilderErrors.InputBuilderError("Could not construct input"))
    }
  }
}

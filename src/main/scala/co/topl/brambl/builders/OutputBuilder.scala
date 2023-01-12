package co.topl.brambl.builders

import co.topl.brambl.builders.BuilderErrors.OutputBuilderError
import co.topl.brambl.builders.Models.OutputBuildRequest
import co.topl.brambl.models.transaction.UnspentTransactionOutput

/**
 * Defines a builder for IoTransaction outputs [[UnspentTransactionOutput]]
 */
trait OutputBuilder {
  /**
   * Construct a IoTransaction output ([[UnspentTransactionOutput]]).
   *
   * @param data The data required to build an UnspentTransactionOutput
   *             The data is an object with the following fields:
   *             datum: Option[Datum.UnspentOutput] - Additional data to include in the built UnspentTransactionOutput
   *             lock: Lock - The lock for the built UnspentTransactionOutput. It will be encoded in the address
   *             value: Value - The value for the built UnspentTransactionOutput
   * @return Either a OutputBuilderError or the built UnspentTransactionOutput
   */
  def constructOutput(data: OutputBuildRequest): Either[OutputBuilderError, UnspentTransactionOutput]
}
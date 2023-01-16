package co.topl.brambl.builders

import co.topl.brambl.builders.BuilderErrors.OutputBuilderError
import co.topl.brambl.models.builders.OutputBuildRequest
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
   *             lock: Lock - The lock for the built UnspentTransactionOutput. It will be encoded in the address
   *             value: Value - The value for the built UnspentTransactionOutput
   *             metadata: Option[SmallData] - Optional metadata to include in the built UnspentTransactionOutput
   *                    If not provided, the built UnspentTransactionOutput's metadata will be empty data
   * @return Either a OutputBuilderError or the built UnspentTransactionOutput
   */
  def constructOutput(data: OutputBuildRequest): Either[OutputBuilderError, UnspentTransactionOutput]
}
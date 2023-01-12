package co.topl.brambl.builders

import co.topl.brambl.builders.Models.{InputBuildRequest, OutputBuildRequest}
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.models.Datum.{IoTransaction => IoTransactionDatum}

/**
 * Defines a builder for [[IoTransaction]]s
 */
trait TransactionBuilder {
  /**
   * Construct an unproven [[IoTransaction]]. The transaction fee is whatever is left over after the sum of the
   * outputs is subtracted from the sum of the inputs. Consequently, any change needs to be explicitly added as an output.
   *
   * A [[co.topl.brambl.models.transaction.SpentTransactionOutput SpentTransactionOutput]] spends an existing
   * [[co.topl.brambl.models.transaction.UnspentTransactionOutput UnspentTransactionOutput]].
   *
   * @param inputRequests A list of data required to build the inputs of this IoTransaction
   *                      Each element represents a single input.
   * @param outputRequests A list of data required to build the outputs of this IoTransaction.
   *                       Each element represents a single output.
   * @param datum Additional data to include in the built IoTransaction.
   *              If not provided, the [[co.topl.brambl.models.transaction.Schedule Schedule]]
   *              within will be defaulted to some reasonable value.
   * @return Either a list of BuilderError or the built IoTransaction
   */
  def constructUnprovenTransaction(
                            inputRequests: List[InputBuildRequest],
                            outputRequests: List[OutputBuildRequest],
                            datum: Option[IoTransactionDatum]
                           ): Either[List[BuilderError], IoTransaction]
}

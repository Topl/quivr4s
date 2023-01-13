package co.topl.brambl.builders

import co.topl.brambl.builders.Models.{InputBuildRequest, OutputBuildRequest}
import co.topl.brambl.models.KnownIdentifier.{TransactionOutput32, TransactionOutput64}
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.models.transaction.Schedule
import quivr.models.SmallData

/**
 * Defines a builder for [[IoTransaction]]s
 */
trait TransactionBuilder {
  /**
   * Construct an unproven [[IoTransaction]]. The transaction fee is whatever is left over after the sum of the
   * LVL outputs is subtracted from the sum of the LVL inputs. Consequently, any change needs to be explicitly added as an output.
   *
   * A [[co.topl.brambl.models.transaction.SpentTransactionOutput SpentTransactionOutput]] spends an existing
   * [[co.topl.brambl.models.transaction.UnspentTransactionOutput UnspentTransactionOutput]].
   *
   * @param inputRequests A list of data required to build the inputs of this IoTransaction
   *                      Each element represents a single input.
   * @param outputRequests A list of data required to build the outputs of this IoTransaction.
   *                       Each element represents a single output.
   * @param schedule The schedule for this IoTransaction
   *                 If not provided, the built transaction will have a default schedule with min set to the current
   *                 slot number, max set to the current slot number + 15000 (approximately 4 hours later),
   *                 and timestamp set to the current time.
   * @param output32Refs A list of identifiers that refer to existing IoTransactions outputs using 32 byte evidences.
   *                     Defaults to an empty list
   * @param output64Refs A list of identifiers that refer to existing IoTransactions outputs using 64 byte evidences.
   *                     Defaults to an empty list
   * @param metadata Optional metadata to include with the built transaction
   *                 If not provided, the built transaction's metadata will be empty data
   *
   * @return Either a list of BuilderError or the built IoTransaction
   */
  def constructUnprovenTransaction(
                                    inputRequests: List[InputBuildRequest],
                                    outputRequests: List[OutputBuildRequest],
                                    schedule: Option[Schedule] = None,
                                    output32Refs: List[TransactionOutput32] = List(),
                                    output64Refs: List[TransactionOutput64] = List(),
                                    metadata: Option[SmallData] = None
                           ): Either[List[BuilderError], IoTransaction]
}

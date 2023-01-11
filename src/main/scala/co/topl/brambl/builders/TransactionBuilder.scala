package co.topl.brambl.builders

import co.topl.brambl.builders.Models.{InputBuildRequest, OutputBuildRequest}
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.models.Datum.{IoTransaction => IoTransactionDatum}

trait TransactionBuilder {
  /**
   * Construct transaction
   *
   * 3:a:A => 1:a:A, 1:a:B, 1:a:C
   * Currently only thinking through single inputs for now so I don't have to think about box selection algorithm
   *
   */
  def constructUnprovenTransaction(
                            inputRequests: List[InputBuildRequest],
                            outputRequests: List[OutputBuildRequest],
                            datum: Option[IoTransactionDatum]
                           ): Either[List[BuilderError], IoTransaction]
}

package co.topl.brambl.builders

import co.topl.brambl.builders.Models.{InputBuildRequest, OutputBuildRequest}
import co.topl.brambl.models.Datum
import co.topl.brambl.models.transaction.{IoTransaction, SpentTransactionOutput, UnspentTransactionOutput}

/**
 * A mock implementation of an [[TransactionBuilder]]
 */
object MockTransactionBuilder extends TransactionBuilder {
  override def constructUnprovenTransaction(
                                             inputRequests: List[InputBuildRequest],
                                             outputRequests: List[OutputBuildRequest],
                                             datum: Option[Datum.IoTransaction]
                                           ): Either[List[BuilderError], IoTransaction] = {
    val inputs = inputRequests
      .map(MockInputBuilder.constructUnprovenInput)
      .partitionMap[BuilderError, SpentTransactionOutput](identity)
    val outputs = outputRequests
      .map(MockOutputBuilder.constructOutput)
      .partitionMap[BuilderError, UnspentTransactionOutput](identity)
    if(inputs._1.isEmpty && outputs._1.isEmpty) {
      // TODO: Supply a default Datum if datum is not provided
      Right(IoTransaction(inputs._2, outputs._2, datum))
    } else
      Left(inputs._1 ++ outputs._1)
  }
}

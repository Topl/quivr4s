package co.topl.brambl.builders

import co.topl.brambl.builders.Models.{InputBuildRequest, OutputBuildRequest}
import co.topl.brambl.models.Datum.{IoTransaction => IoTransactionDatum}
import co.topl.brambl.models.transaction.{IoTransaction, SpentTransactionOutput, UnspentTransactionOutput}


object MockTransactionBuilder extends TransactionBuilder {
  // Change needs to be explicitly added as an output.
  // Fee is whatever is left over after the sum of the outputs is subtracted from the sum of the inputs.
  override def constructUnprovenTransaction(
                                             inputRequests: List[InputBuildRequest],
                                             outputRequests: List[OutputBuildRequest],
                                             datum: Option[IoTransactionDatum]
                                           ): Either[List[BuilderError], IoTransaction] = {
    val inputs = inputRequests
      .map(MockInputBuilder.constructUnprovenInput)
      .partitionMap[BuilderError, SpentTransactionOutput](identity)
    val outputs = outputRequests
      .map(MockOutputBuilder.constructOutput)
      .partitionMap[BuilderError, UnspentTransactionOutput](identity)
    if(inputs._1.isEmpty && outputs._1.isEmpty)
      Right(IoTransaction(inputs._2, outputs._2, datum))
    else
      Left(inputs._1 ++ outputs._1)
  }
}

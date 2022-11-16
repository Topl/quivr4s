package co.topl.brambl

// Functions related to Transaction Builder
// These functions are based off of the Transaction Diagram in TSDK-173
object TransactionBuilder {
  // Create the list of inputs for a transaction
  def buildInputs(
                   utxos: List[Models.IoTx.UnspentOutput],
                   sparsePredicates: List[Models.Predicate.Sparse]
                 ): List[Models.IoTx.UnprovenSpentOutput] = {
    ???
  }

  // Create unproven transaction
  def unprovenTransactionBuilder(
                                  inputs: List[Models.IoTx.UnprovenSpentOutput],
                                  outputs: List[Models.IoTx.UnspentOutput],
                                  schedule: Models.IoTx.Schedule,
                                  metadata: Option[Array[Byte]]
                                ): Models.UnprovenTx  = {
    ???
  }
}

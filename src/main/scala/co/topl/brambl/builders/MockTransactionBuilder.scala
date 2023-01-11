package co.topl.brambl.builders

import co.topl.brambl.models.Indices
import co.topl.brambl.models.Datum.{IoTransaction => IoTransactionDatum, SpentOutput => SpentOutputDatum, UnspentOutput => UnspentOutputDatum}
import co.topl.brambl.models.box.{Lock, Value}
import co.topl.brambl.models.transaction.{IoTransaction, SpentTransactionOutput}


object MockTransactionBuilder extends TransactionBuilder {
  // Change needs to be explicitly added as an output.
  // Fee is whatever is left over after the sum of the outputs is subtracted from the sum of the inputs.
  override def constructUnprovenTransaction(
                                    inputIndices: List[Indices],
                                    inputDatums: List[Option[SpentOutputDatum]],
                                    outputIndices: List[Indices], // The output indices need to end up as a list of addresses
                                                                  // Addresses contain the identifier
                                                                  // The identifier will need to encode evidence of the lock
                                    outputDatums: List[Option[UnspentOutputDatum]],
                                    locks: List[Lock],
                                    outputValues: List[Value],
                                    datum: Option[IoTransactionDatum]
                                  ): Either[BuilderError, IoTransaction] = {
    // TODO: Ensure parallel lists are the same length
    val builtInputs = for (i <- inputIndices.indices.toList)
      yield MockInputBuilder.constructUnprovenInput(inputIndices(i), inputDatums(i))
    val x: (List[BuilderError], List[SpentTransactionOutput]) = builtInputs.partitionMap[BuilderError, SpentTransactionOutput]
    val builtOutputs = for (i <- outputIndices.indices)
      yield MockOutputBuilder.constructOutput(outputIndices(i), outputDatums(i), locks(i), outputValues(i))

  }
}

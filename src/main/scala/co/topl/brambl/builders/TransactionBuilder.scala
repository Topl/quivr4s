package co.topl.brambl.builders

import co.topl.brambl.models.{Address, Indices}
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.models.Datum.{IoTransaction => IoTransactionDatum, SpentOutput => SpentOutputDatum, UnspentOutput => UnspentOutputDatum}
import co.topl.brambl.models.box.Lock

trait TransactionBuilder {
  // Only considering single inputs for now so I don't have to think about box selection algorithm
  /**
   * Construct simple transaction
   *
   * 3:a:A => 1:a:A, 1:a:B, 1:a:C
   *
   * Like above but considers the locks of the outputs
   *
   * TODO: Create some kind of request object to encompass the parallel lists
   */
  def constructTransaction(
                            inputIndices: List[Indices],
                            inputDatums: List[Option[SpentOutputDatum]],
                            outputIndices: List[Indices], // The output indices need to end up as a list of addresses
                                                          // Addresses contain the identifier
                                                          // The identifier will need to encode evidence of the lock
                            outputDatums: List[Option[UnspentOutputDatum]],
                            locks: List[Lock],
                            quantities: List[Long],
                            datum: Option[IoTransactionDatum]
                           ): Either[BuilderError, IoTransaction]
}

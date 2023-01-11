package co.topl.brambl.builders

import co.topl.brambl.models.{Address, Indices}
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.models.Datum.{IoTransaction => IoTransactionDatum}

trait TransactionBuilder {
  // Only considering single inputs for now so I don't have to think about box selection algorithm
  /**
   * Construct simple transaction
   *
   * 3:a:A => 3:a:B
   *
   * Any combination of:
   * By Address
   * By Indices
   * By KnownIdentifiers
   *
   * instead, will just be indices
   *  can fetch indices by Address, indices, KnownIdentifiers
   *  If an Address, index, or knownIdentifier is not associated to an index, can register it to a self-index
   */
//  def constructTransaction(inputAddress: Address, outputAddress: Address, datum: Option[IoTransactionDatum]): Option[IoTransaction]
  def constructTransaction(
                            inputIndices: Indices,
                            outputIndices: Indices,
                            datum: Option[IoTransactionDatum]
                          ): Either[BuilderError, IoTransaction]

  /**
   * Construct simple transaction
   *
   * 3:a:A => 1:a:A, 2:a:B
   *
   * Like above will search by indices
   * but can have change
   *
   * Put quantity into address at output indices. The remaining quantity will stay at the address in
   *
   *
   * Will error out if amount is not enough
   */
  def constructTransaction2(
                             inputIndices: Indices,
                             outputIndices: Indices,
                             quantity: Long,
                             datum: Option[IoTransactionDatum]
                           ): Either[BuilderError, IoTransaction]


  /**
   * Construct simple transaction
   *
   * 1:a:A, 1:a:B, 1:a:C
   *
   * List of quantities need to match the list of outputIndices.
   *
   * Like above but need to move separate quantities in different addresses
   */
  def constructTransaction3(
                             inputIndices: Indices,
                             outputIndices: List[Indices],
                             quantities: List[Long],
                             datum: Option[IoTransactionDatum]
                           ): Either[BuilderError, IoTransaction]
}

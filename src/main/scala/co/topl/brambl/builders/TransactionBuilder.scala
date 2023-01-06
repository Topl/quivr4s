package co.topl.brambl.builders

import co.topl.brambl.models.Address
import co.topl.brambl.models.transaction.IoTransaction

trait TransactionBuilder {
  // Construct simple transaction
  def constructTransaction(inputAddress: Address, outputAddress: Address): Option[IoTransaction]
}

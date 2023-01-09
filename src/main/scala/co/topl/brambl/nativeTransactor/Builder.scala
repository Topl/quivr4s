package co.topl.brambl.nativeTransactor

import co.topl.brambl.models.Address
import co.topl.brambl.models.transaction.IoTransaction

trait Builder {
  // Construct simple transaction
  def constructTransaction(inputAddress: Address, outputAddress: Address): Option[IoTransaction]
}

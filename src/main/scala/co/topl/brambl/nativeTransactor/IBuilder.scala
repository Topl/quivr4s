package co.topl.brambl.nativeTransactor

import co.topl.node.Address
import co.topl.node.transaction.IoTransaction

trait IBuilder {
  // Construct simple transaction
  def constructTransaction(inputAddress: Address, outputAddress: Address): Option[IoTransaction]
}

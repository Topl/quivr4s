package co.topl.bramb

import co.topl.node.transaction.IoTransaction

//// JAA - Credentials should have the single job of taking an unproven transaction and converting it to a proven one. Nothing else should be exposed I think

object Credentials {
  def prove(unprovenTx: IoTransaction): IoTransaction = ???
}
package co.topl.brambl
import co.topl.node.Tetra.Box
import co.topl.node.Tetra.IoTx
import co.topl.quivr.SignableTxBytes


// Meant to emulate what Credentials will be in the SDK
// Credentials knows secret information and state
// Will return toy data (secrets, utxo states, etc)

// JAA - Credentials should have the single job of taking an unproven transaction and converting it to a proven one. Nothing else should be exposed I think
object Credentials {

  // Get Utxo by Box ID
  def getUnspentOutput(id: Box.Id): IoTx.UnspentOutput = ???

  // get Transaction by Box Id.
  // Should also add functions to search by Transaction Id and cartesian indices
  def getIoTxByBox(id: Box.Id): IoTx = ???
}

package co.topl.brambl
// Meant to emulate what Credentials will be in the SDK
// Credentials knows secret information and state
// Will return toy data (secrets, utxo states, etc)


import co.topl.node.Tetra.Box
import co.topl.node.Tetra.IoTx
import co.topl.quivr.SignableTxBytes

object Credentials {

  // Get Utxo by Box ID
  def getUnspentOutput(id: Box.Id): IoTx.UnspentOutput = ???

  // get Transaction by Box Id.
  // Should also add functions to search by Transaction Id and cartesian indices
  def getIoTxByBox(id: Box.Id): Tx = ???
}

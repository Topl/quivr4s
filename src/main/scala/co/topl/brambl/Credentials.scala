// Meant to emulate what Credentials will be in the SDK
// Credentials knows secret information and state
// Will return toy data (secrets, utxo states, etc)

package co.topl.brambl

import co.topl.brambl.Tetra.Box
import co.topl.brambl.Tetra.IoTx
import co.topl.quivr.SignableTxBytes

trait Tx extends IoTx {
  // Temporarily abstracting away how the signableBytes will be retrieved from an IoTx
  def getSignableBytes: SignableTxBytes
}

object Credentials {

  // Get Utxo by Box ID
  def getUnspentOutput(id: Box.Id): IoTx.UnspentOutput = ???

  // get Transaction by Box Id.
  // Should also add functions to search by Transaction Id and cartesian indices
  def getIoTxByBox(id: Box.Id): Tx = ???
}

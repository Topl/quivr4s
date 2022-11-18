package co.topl.brambl
import co.topl.node.Tetra.Box
import co.topl.node.Tetra.IoTx
import co.topl.quivr.SignableTxBytes

// Meant to emulate what Credentials will be in the SDK
// Credentials knows secret information and state
// Will return toy data (secrets, utxo states, etc)

// JAA - Credentials should have the single job of taking an unproven transaction and converting it to a proven one. Nothing else should be exposed I think


import co.topl.node.Tetra.Box
import co.topl.node.Tetra.Predicate
import co.topl.node.Models.SignableBytes
import co.topl.crypto.hash.blake2b256
import co.topl.common.Digests
import co.topl.brambl.Models._
import co.topl.node.Tetra

/***
 *
 * x, y, z all are 2^32^ -1
 *
 * values from 0 to 2^31^ -1 are all user defined
 * values from 2^31^ to 2^32^-1
 */
object Credentials {
  // get already existing utxo by Box Id.
  def getBoxById(id: Box.Id): Box = ???

  // Get the (x,y,z) indices that map to the utxo given by the Box Id
  def getIndicesByBoxId(boxId: Box.Id): Indices = {
    val idxArr = new String(boxId.bytes).split(',')
    Indices(idxArr(0).toInt, idxArr(1).toInt, idxArr(2).toInt)
  }

  /**
   * Get the ID of the box whose associated UTxO maps to the indices (x, y, z)
  * */
  def getBoxId(idx: Indices): Box.Id = Box.Id(s"${idx.x},${idx.y},${idx.z}".getBytes)

  /**
   * Get the box whose associated UTxO maps to the indices (x, y, z)
   * */
  def getBox(idx: Indices): Box = getBoxById(getBoxId(idx))

  private def getPreImageBytes(idx: Indices): Array[Byte] = s"x${idx.x}.y${idx.y}.z${idx.z}".getBytes

  /**
   * Assuming that the proof associated to indices (x,y,z) is a digest proof, return it's preimage
   * */
  def getDigestPreImage(idx: Indices): Digests.Preimage = Digests.Preimage(getPreImageBytes(idx), 0)

  /**
   * Assuming that the proof associated to indices (x,y,z) is a digest proof, return the digest
   * */
  def getDigest(idx: Indices): Digests.Digest =
    Digests.Digest(blake2b256.hash(getPreImageBytes(idx)).value)


  // prove an unproven input (digest proof)
  def proveInput(unprovenInput: UnprovenSpentOutput, message: SignableBytes): Tetra.IoTx.SpentOutput = {
    val predicateImage = getBoxById(unprovenInput.reference).image
    val preImage = getDigestPreImage(getIndicesByBoxId(unprovenInput.reference))
    val proof = Option(QuivrService.getDigestProof(preImage, message))
    val attestation = Tetra.Attestation(predicateImage, unprovenInput.knownPredicate, List(proof))

    Tetra.IoTx.SpentOutput(
      unprovenInput.reference,
      attestation,
      unprovenInput.value,
      unprovenInput.datum
    )
  }

  // Prove an unproven transaction
  def proveTransaction(unprovenTx: UnprovenIoTx): Tetra.IoTx = {
    val message = unprovenTx.getSignableBytes

    val unprovenInput = unprovenTx.inputs.head
    val provenInput = proveInput(unprovenInput, message)

    Tetra.IoTx(List(provenInput), unprovenTx.outputs, unprovenTx.schedule, unprovenTx.metadata)
  }
}

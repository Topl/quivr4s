package co.topl.brambl

import co.topl.node.Tetra.Box
import co.topl.node.Models.SignableBytes
import co.topl.crypto.hash.{blake2b256, blake2b512}
import co.topl.common.{Digest, Preimage}
import co.topl.brambl.Models._
import co.topl.node.Tetra
import co.topl.genus.Models.Txo

// Meant to emulate what Credentials will be in the SDK
// Credentials knows secret information and state
// Will return toy data (secrets, utxo states, etc)


// JAA - Credentials should have the single job of taking an unproven transaction and converting it to a proven one. Nothing else should be exposed I think

/***
 *
 * x, y, z all are 2^32^ -1
 *
 * values from 0 to 2^31^ -1 are all user defined
 * values from 2^31^ to 2^32^-1
 */
object Credentials {
  // get already existing utxo by Box Id.
  private def getBoxById(id: Box.Id): Box = ???

  private  def getTxoByBoxId(id: Box.Id): Txo = ???

  // Get the (x,y,z) indices that map to the utxo given by the Box Id
  private def getIndicesByBoxId(boxId: Box.Id): Indices = {
    val idxArr = new String(boxId.bytes).split(',')
    Indices(idxArr(0).toInt, idxArr(1).toInt, idxArr(2).toInt)
  }

  /**
   * Get the ID of the box whose associated UTxO maps to the indices (x, y, z)
  * */
  private def getBoxId(idx: Indices): Box.Id = Box.Id(s"${idx.x},${idx.y},${idx.z}".getBytes)

  /**
   * Get the Txo whose associated UTxO maps to the indices (x, y, z)
   *
   * perhaps in a Storage or Interface module?
   *
   * */
  def getTxo(idx: Indices): Txo = getTxoByBoxId(getBoxId(idx))

  private def getPreImageBytes(idx: Indices): Array[Byte] = s"x${idx.x}.y${idx.y}.z${idx.z}".getBytes

  /**
   * Assuming that the proof associated to indices (x,y,z) is a digest proof, return it's preimage
   * */
  private def getDigestPreImage(idx: Indices): Preimage = Preimage(getPreImageBytes(idx), Array(0: Byte))

  /**
   * Assuming that the proof associated to indices (x,y,z) is a digest proof, return the digest
   *
   * perhaps in a Storage or Interface module?
   * */
  def getDigest(idx: Indices): Digest =
    Digest(blake2b256.hash(getPreImageBytes(idx)).value)

  // prove an unproven input (digest proof). indices version
  private def proveSpentOutputV1(unprovenInput: UnprovenSpentOutputV1, message: SignableBytes): Tetra.IoTx.SpentOutput = {
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

  // Prove an unproven transaction. indices version
  def proveIoTxV1(unprovenTx: UnprovenIoTxV1): Tetra.IoTx = {
    val message = unprovenTx.getSignableBytes

    val unprovenInput = unprovenTx.inputs.head
    val provenInput = proveSpentOutputV1(unprovenInput, message)

    Tetra.IoTx(List(provenInput), unprovenTx.outputs, unprovenTx.schedule, unprovenTx.metadata)
  }

  // prove an unproven input (digest proof). txo version
  private def proveSpentOutputV2(unprovenInput: UnprovenSpentOutputV2, message: SignableBytes): Tetra.IoTx.SpentOutput = {
    val predicateImage = getBoxById(unprovenInput.reference).image
    val idx = getIndicesByBoxId(unprovenInput.reference)
    val preImage = getDigestPreImage(idx)
    val proof = Option(QuivrService.getDigestProof(preImage, message))
    val knownPredicate = List(Option(QuivrService.getDigestProposition(getDigest(idx))))
    val attestation = Tetra.Attestation(predicateImage, Tetra.Predicate.Known(knownPredicate), List(proof))

    Tetra.IoTx.SpentOutput(
      unprovenInput.reference,
      attestation,
      unprovenInput.value,
      unprovenInput.datum
    )
  }

  // Prove an unproven transaction. txo version
  def proveIoTxV2(unprovenTx: UnprovenIoTxV2): Tetra.IoTx = {
    val message = unprovenTx.getSignableBytes

    val unprovenInput = unprovenTx.inputs.head
    val provenInput = proveSpentOutputV2(unprovenInput, message)

    Tetra.IoTx(List(provenInput), unprovenTx.outputs, unprovenTx.schedule, unprovenTx.metadata)
  }

  /**
   * Get the next set of usable (x,y,z) indices.
   *
   * perhaps in a Storage or Interface module?
   * */
  def getNextIndices: Indices = ???

}

package co.topl.brambl

import co.topl.brambl.Models.Indices
import co.topl.common.{Digest, Preimage}
import co.topl.crypto.hash.blake2b256
import co.topl.genus.Models.Txo
import co.topl.node.Tetra.Box

/***
 *
 * x, y, z all are 2^32^ -1
 *
 * values from 0 to 2^31^ -1 are all user defined
 * values from 2^31^ to 2^32^-1
 */
object Storage {
  // get already existing utxo by Box Id.
  def getBoxById(id: Box.Id): Box = ???

  def getTxoByBoxId(id: Box.Id): Txo = ???

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
   * Get the Txo whose associated UTxO maps to the indices (x, y, z)
   *
   * perhaps in a Storage or Interface module?
   *
   * */
  def getTxo(idx: Indices): Txo = getTxoByBoxId(getBoxId(idx))

  def getPreImageBytes(idx: Indices): Array[Byte] = s"x${idx.x}.y${idx.y}.z${idx.z}".getBytes

  /**
   * Assuming that the proof associated to indices (x,y,z) is a digest proof, return it's preimage
   * */
  def getDigestPreImage(idx: Indices): Preimage = Preimage(getPreImageBytes(idx), Array(0: Byte))

  /**
   * Assuming that the proof associated to indices (x,y,z) is a digest proof, return the digest
   *
   * perhaps in a Storage or Interface module?
   * */
  def getDigest(idx: Indices): Digest =
    Digest(blake2b256.hash(getPreImageBytes(idx)).value)


  /**
   * Get the next set of usable (x,y,z) indices.
   *
   * perhaps in a Storage or Interface module?
   * */
  def getNextIndices: Indices = ???
}

package co.topl.brambl

import co.topl.brambl.Models.Indices
import co.topl.common.Models.{Digest, Preimage}
import co.topl.crypto.hash.blake2b256
import co.topl.genus.TransactionOutput
import co.topl.node.FullPredicate
import co.topl.node.box.Box

/**
 * *
 *
 * x, y, z all are 2^32^ -1
 *
 * values from 0 to 2^31^ -1 are all user defined
 * values from 2^31^ to 2^32^-1
 */
object Storage {

  // Dummy values
  val boxes: Map[Box.Id, Box] = Map(
    Box.Id("0,0,1".getBytes) -> Box(
      FullPredicate.Image(
        Array(0: Byte),
        1
      ),
      Box.Values.Token(1)
    ),
    Box.Id("0,0,2".getBytes) -> Box(
      FullPredicate.Image(
        Array(0: Byte),
        1
      ),
      Box.Values.Token(1)
    ),
    Box.Id("0,0,3".getBytes) -> Box(
      FullPredicate.Image(
        Array(0: Byte),
        1
      ),
      Box.Values.Token(1)
    ),
    Box.Id("5,5,5".getBytes) -> Box(
      FullPredicate.Image(
        Array(0: Byte),
        1
      ),
      Box.Values.Token(1)
    )
  )

  /**
   * Get the ID of the box whose associated UTxO maps to the indices (x, y, z)
   */
  def getBoxId(idx: Indices): Box.Id = Box.Id(s"${idx.x},${idx.y},${idx.z}".getBytes)

  /**
   * Get the (x,y,z) indices that map to the utxo given by the Box Id
   */
  def getIndicesByBoxId(boxId: Box.Id): Indices = {
    val idxArr = new String(boxId.bytes).split(',')
    Indices(idxArr(0).toInt, idxArr(1).toInt, idxArr(2).toInt)
  }

  /**
   * Get an already existing box by its ID
   */
  def getBoxById(id: Box.Id): Box = boxes.getOrElse(id, None)

  /**
   * Get an already existing genus TXO by its box ID
   */
  def getTxoByBoxId(id: Box.Id): TransactionOutput = {
    val box = getBoxById(id)

    TransactionOutput(id, box.value, box.predicate.generateAddress)
  }

  /**
   * Get the Txo whose associated UTxO maps to the indices (x, y, z)
   *
   * perhaps in a Storage or Interface module?
   */
  def getTxo(idx: Indices): TransactionOutput = getTxoByBoxId(getBoxId(idx))

  /**
   * Get the next set of usable (x,y,z) indices.
   *
   * perhaps in a Storage or Interface module?
   */
  def getNextIndices: Indices = Indices(5, 5, 5) // arbitrary

  // Not sure if the following 3 functions should exist in the actual implementation
  // I.e., Should SDK provide (x,y,z) => preimage/digest?
  /**
   * Given indices (x,y,z) return the corresponding preimage bytes
   */
  private def getPreImageBytes(idx: Indices): Array[Byte] = s"x${idx.x}.y${idx.y}.z${idx.z}".getBytes

  /**
   * Assuming that the proof associated to indices (x,y,z) is a digest proof, return it's preimage
   */
  def getDigestPreImage(idx: Indices): Preimage = Preimage(getPreImageBytes(idx), Array(0: Byte))

  /**
   * Assuming that the proof associated to indices (x,y,z) is a digest proof, return the digest
   */
  def getDigest(idx: Indices): Digest = Digest(blake2b256.hash(getPreImageBytes(idx)).value)

}

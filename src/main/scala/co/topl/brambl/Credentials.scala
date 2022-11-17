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
import co.topl.node.Models.Root
import co.topl.quivr.User.Digests
import co.topl.crypto.hash.blake2b256

case class Indices(x: Int, y: Int, z: Int)

// CRUD functions related to wallet storage
object Storage {
  // get already existing utxo by Box Id.
  def getBoxById(id: Box.Id): Box = ???

  // get known predicate. Useful since address.evidence is a predicate's ID
  // enables address => Predicate.Known
  def getKnownPredicateById(id: Predicate.Id): Predicate.Known = ???

  // get known predicate. Useful since a box has a predicate image and
  // a predicate image has the root.
  // enabled Box => Predicate.Known
  def getKnownPredicateByRoot(root: Root): Predicate.Known = ???

  // get the image of the predicate associated with a specific box
  def getPredicateImageByBoxId(id: Box.Id): Predicate.Image = getBoxById(id).image

  // Get the (x,y,z) indices that map to the utxo given by the Box Id
  def getIndicesByBoxId(boxId: Box.Id): Indices = ???
}

/***
 *
 * x, y, z all are 2^32^ -1
 *
 * values from 0 to 2^31^ -1 are all user defined
 * values from 2^31^ to 2^32^-1
 */
object Credentials {
  /**
   * Credentials.proveTx(unprovenTx) => ProvenTx
   *
   * unprovenTx has
   * - unproven inputs; boxId, boxValue, metadata, unproven***Attestation (i.e, SparsePredicate)
   * - outputs; address, boxValue, metadata
   * - schedule
   * - metadata
   *
   * To get a proven transaction, we want to convert the unprovenAttestation (SparsePredicate) to a proven attestation
   *
   * A [proven] Attestation requires:
   *  - predicate image
   *  -
   * A sparse predicate has:
   *  -
   *
   * */
//  def proveTx(unproven tx): Models.ProvenTx = ???

  /**
   * Get the ID of the box whose associated UTxO maps to the indices (x, y, z)
  * */
  def getBoxId(idx: Indices): Box.Id = Box.Id(s"box${idx.x}${idx.y}${idx.z}".getBytes)

  /**
   * Get the box whose associated UTxO maps to the indices (x, y, z)
   * */
  def getBox(idx: Indices): Box = Storage.getBoxById(getBoxId(idx))

  /**
   * Get a proposition s
   * */
  def getKnownPredicate(idx: Indices): Predicate.Known = Storage.getKnownPredicateByRoot(
    getBox(idx).image.root
   )

  private def getPreImageBytes(idx: Indices): Array[Byte] = s"x${idx.x}.y${idx.y}.z${idx.z}".getBytes

  /**
   * Assuming that the proof associated to indices (x,y,z) is a digest proof, return it's preimage
   * */
  def getDigestPreImage(idx: Indices): Digests.Preimage = Digests.Preimage(getPreImageBytes(idx))

  /**
   * Assuming that the proof associated to indices (x,y,z) is a digest proof, return the digest
   * */
  def getDigest(idx: Indices): Digests.Digest = Digests.Digest(
    1: Byte, // Arbitrary byte for now
    blake2b256.hash(getPreImageBytes(idx)).value
  )
}

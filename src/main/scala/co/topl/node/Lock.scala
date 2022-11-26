package co.topl.node

import co.topl.crypto.accumulators.LeafData
import co.topl.crypto.accumulators.merkle.MerkleTree
import co.topl.crypto.hash.Blake2b
import co.topl.crypto.hash.digest.Digest32
import co.topl.crypto.implicits.{blake2b256Hash, digestDigest32}
import co.topl.node.typeclasses.ContainsEvidence.Ops
import co.topl.node.typeclasses.ContainsSignable.instances.propositionSignable
import co.topl.node.typeclasses.Evidence
import co.topl.quivr.Proposition

sealed abstract class Lock

// Predicate -> Image -> Commitment -> Identifier
object Locks {
  // Private information
  // this should probably be a non-empty chain, but how to enforce across other languages?
  case class Predicate(challenges: List[Proposition], threshold: Int) extends Lock

  // Semi-public information
  // The most commonly shared construction between parties
  case class Image(leaves: List[Evidence], threshold: Int) extends Lock

  // Public information
  // Predicate Commitments are used to encumber boxes
  // use a Root here so we can provide a membership proof of the conditions
  case class Commitment(root: Root, size: Int, threshold: Int) extends Lock

  def image(predicate: Predicate): Image =
    Image(
      predicate.challenges.map(_.evidence),
      predicate.threshold
    )

  def commit(image: Image): Commitment =
    Commitment(
      MerkleTree.apply[Blake2b, Digest32](image.leaves.map(l => LeafData(l.bytes)).toSeq).rootHash.value,
      image.leaves.size,
      image.threshold
    )
}



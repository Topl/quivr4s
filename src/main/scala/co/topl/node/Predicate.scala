package co.topl.node

import co.topl.crypto.accumulators.LeafData
import co.topl.crypto.accumulators.merkle.MerkleTree
import co.topl.crypto.hash.{Blake2b, blake2b256}
import co.topl.crypto.hash.digest.Digest32
import co.topl.crypto.implicits.{blake2b256Hash, digestDigest32}
import co.topl.node.typeclasses.ProposedEvidence.Ops
import co.topl.node.typeclasses.ProposedEvidence.instances._
import co.topl.quivr.Proposition

case class Predicate(challenges: List[Proposition], threshold: Int)

// Predicate -> Image -> Commitment -> Identifier
object Predicate {
  case class Known(challenges: List[Option[Proposition]])

  case class Image(leaves: List[PropositionEvidence], threshold: Int)

  // use a Root here so we can provide a membership proof of the conditions
  case class Commitment(root: Root, numChallenges: Int, threshold: Int)

  def image(predicate: Predicate): Image =
    Image(
      predicate.challenges.map(_.evidence),
      predicate.threshold
    )

  def commit(image: Image): Commitment =
    Commitment(
      MerkleTree.apply[Blake2b, Digest32](image.leaves.map(LeafData(_)).toSeq).rootHash.value,
      image.leaves.size,
      image.threshold
    )
}



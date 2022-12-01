package co.topl.node.box

import co.topl.node.Identifiers
import co.topl.node.typeclasses.ContainsEvidence.{ListOps, SignableOps}
import co.topl.node.typeclasses.ContainsSignable.instances._
import co.topl.quivr.Proposition

// should be able to calculate or retrieve the root of the Lock
// - from Predicate -> Image -> Commitment
// - from Image -> Commitment
// - from Commitment -> _.root
sealed abstract class Lock

// Predicate -> Image -> Commitment -> Signable -> Evidence -> Identifier -> Address -> Reference
object Locks {
  // Private information
  // this should probably be a non-empty chain, but how to enforce across other languages?
  case class Predicate(challenges: List[Proposition], threshold: Int) extends Lock

  // Semi-public information
  // The most commonly shared construction between parties
  case class Image32(leaves: List[Identifiers.Lock32], threshold: Int) extends Lock
  case class Image64(leaves: List[Identifiers.Lock64], threshold: Int) extends Lock

  // Public information
  // Predicate Commitments are used to encumber boxes
  // use a Root here so we can provide a membership proof of the conditions
  case class Commitment32(root: Identifiers.AccumulatorRoot32, size: Int, threshold: Int) extends Lock
  case class Commitment64(root: Identifiers.AccumulatorRoot64, size: Int, threshold: Int) extends Lock

  def image32(predicate: Predicate): Image32 =
    Image32(
      predicate.challenges.map(c => Identifiers.Lock32(c.blake2bEvidence.sized32Evidence)),
      predicate.threshold
    )

  def image64(predicate: Predicate): Image64 =
    Image64(
      predicate.challenges.map(c => Identifiers.Lock64(c.blake2bEvidence.sized64Evidence)),
      predicate.threshold
    )

  def commit32(image: Image32): Commitment32 =
    Commitment32(
      Identifiers.AccumulatorRoot32(image.leaves.merkleEvidence.sized32Evidence),
      image.leaves.size,
      image.threshold
    )

  def commit64(image: Image64): Commitment64 =
    Commitment64(
      Identifiers.AccumulatorRoot64(image.leaves.merkleEvidence.sized64Evidence),
      image.leaves.size,
      image.threshold
    )
}



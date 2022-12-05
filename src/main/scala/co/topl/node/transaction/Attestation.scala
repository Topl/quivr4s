package co.topl.node.transaction

import co.topl.node.box.{Lock, Locks}
import co.topl.quivr.{Proof, Proposition}

/**
 * Attestation reveals responses to a specified Lock. An attestation is verified to ensure the veracity of the attestation.
 * A list of options is provided to facilitate multi-signature verification methods where only some proods are known.
 */
sealed abstract class Attestation {
  val lock: Lock
  val responses: List[Option[Proof]]
}

object Attestations {
  case class Predicate(lock: Locks.Predicate, responses: List[Option[Proof]])
    extends Attestation

  case class Image32(lock: Locks.Image32, known: List[Option[Proposition]], responses: List[Option[Proof]])
      extends Attestation

  case class Image64(lock: Locks.Image64, known: List[Option[Proposition]], responses: List[Option[Proof]])
    extends Attestation

  case class Commitment32(lock: Locks.Commitment32, known: List[Option[Proposition]], responses: List[Option[Proof]])
      extends Attestation

  case class Commitment64(lock: Locks.Commitment64, known: List[Option[Proposition]], responses: List[Option[Proof]])
    extends Attestation
}

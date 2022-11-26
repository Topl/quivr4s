package co.topl.node

import co.topl.quivr
import co.topl.quivr.{Proof, Proposition}

sealed abstract class Attestation {
  val lock: Lock
}

object Attestations {
  val default: List[Option[Proof]] = List(Some(quivr.Models.Primitive.Locked.Proof()))

  case class Predicate(lock: Locks.Predicate, responses: List[Option[Proof]]) extends Attestation

  case class Image(lock: Locks.Image, known: List[Option[Proposition]], responses: List[Option[Proof]])
      extends Attestation

  case class Commitment(lock: Locks.Commitment, known: List[Option[Proposition]], responses: List[Option[Proof]])
      extends Attestation
}

package co.topl.node.transaction

import co.topl.node.box.{Lock, Locks}
import co.topl.quivr
import co.topl.quivr.{Proof, Proposition}

sealed abstract class Attestation {
  val lock: Lock
}

object Attestations {
  val default: List[Option[Proof]] = List(Some(quivr.Models.Primitive.Locked.Proof()))

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

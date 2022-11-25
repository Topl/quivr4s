package co.topl.node

import co.topl.quivr

case class Attestation(image: Predicate.Commitment, known: Predicate.Known, responses: List[Option[quivr.Proof]])

object Attestation {
  val default: List[Option[quivr.Proof]] = List(Some(quivr.Models.Primitive.Locked.Proof()))
}

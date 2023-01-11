package co.topl.brambl.builders

import cats.implicits.catsSyntaxOptionId
import co.topl.brambl.models.box.Lock
import co.topl.brambl.models.transaction.Attestation
import quivr.models.Proof

object MockAttestationBuilder extends AttestationBuilder {
  override def constructUnprovenAttestation(lock: Lock): Either[BuilderError, Attestation] = lock.value match {
    case Lock.Value.Predicate(p) => Right(
      Attestation().withPredicate(
        Attestation.Predicate(
          p.some,
          List.fill(p.challenges.length)(Proof()) // Its unproven
        )
      )
    )
    case _ => Left(BuilderErrors.AttestationBuilderError("Only considering Predicate locks for now"))
  }
}

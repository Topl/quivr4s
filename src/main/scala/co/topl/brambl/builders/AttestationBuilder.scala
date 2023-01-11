package co.topl.brambl.builders

import co.topl.brambl.models.box.Lock
import co.topl.brambl.models.transaction.Attestation

trait AttestationBuilder {
  def constructUnprovenAttestation(lock: Lock): Either[BuilderError, Attestation]
}

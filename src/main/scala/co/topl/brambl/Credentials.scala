package co.topl.brambl

import co.topl.node.transaction.{IoTransaction, SpentOutput, Attestations}
import co.topl.node.typeclasses.ContainsSignable.instances._
import co.topl.node.typeclasses.ContainsSignable

//// JAA - Credentials should have the single job of taking an unproven transaction and converting it to a proven one. Nothing else should be exposed I think

object Credentials {
  // A test should be done to ensure the signable bytes of the input and output tx did not change
  def prove(unprovenTx: IoTransaction): IoTransaction = {
    val signable = ContainsSignable[IoTransaction].signableBytes(unprovenTx)

    def proveInput(input: SpentOutput): SpentOutput = {
      val attestations = input.attestation match {
        case Attestations.Predicate(predLock, _) => Attestations.Predicate(
          predLock,
          predLock.challenges.map(_ => Option(QuivrService.digestProof(signable)))
        )
        case _ => ???
      }
      SpentOutput(input.reference, attestations, input.value, input.datum, input.opts)
    }

    // We are only worrying about 1 of 1 Digest predicates for now
    val inputs = unprovenTx.inputs.map(proveInput)
    IoTransaction(
      inputs,
      unprovenTx.outputs, unprovenTx.datum, unprovenTx.opts
    )
  }
}
package co.topl.brambl

import co.topl.brambl.Models.Indices
import co.topl.node.transaction.{Attestations, IoTransaction, SpentTransactionOutput}
import co.topl.node.typeclasses.ContainsSignable.instances.ioTransactionSignable
import co.topl.common.Models.{Digest, DigestVerification, Preimage}
import co.topl.quivr.Models.Primitive
import co.topl.quivr.{Proof, Proposition, SignableBytes}

//// JAA - Credentials should have the single job of taking an unproven transaction and converting it to a proven one. Nothing else should be exposed I think

object Credentials {

  private def getProof(msg: SignableBytes, proposition: Proposition, idx: Indices): Option[Proof] = {
    proposition match {
      case _: Primitive.Locked.Proposition => Some(QuivrService.lockedProof(msg))
      case _: Primitive.Digest.Proposition => {
        // replace salt with randomized bytes
        val preimage = Preimage(Wallet.getSecret(idx), "salt".getBytes)
        Some(QuivrService.digestProof(msg, preimage))
      }
      case _ => None
    }
  }

  private def proveInput(input: SpentTransactionOutput, msg: SignableBytes): SpentTransactionOutput = {
    val idx = Wallet.getIndicesByIdentifier(input.knownIdentifier)
    val attestations = input.attestation match {
      case Attestations.Predicate(predLock, _) => Attestations.Predicate(
        predLock,
        predLock.challenges.map(prop => getProof(msg, prop, idx))
      )
      case _ => ??? // We are not handling other types of Attestations at this moment in time
    }
    SpentTransactionOutput(input.knownIdentifier, attestations, input.value, input.datum, input.opts)
  }

  // A test should be done to ensure the signable bytes of the input and output tx did not change
  def prove(unprovenTx: IoTransaction): IoTransaction = {
    val signable = ioTransactionSignable.signableBytes(unprovenTx)
    val inputs = unprovenTx.inputs.map(proveInput(_, signable))

    IoTransaction(inputs, unprovenTx.outputs, unprovenTx.datum)
  }
}
package co.topl.brambl

import co.topl.brambl.Models.Indices
import co.topl.node.transaction.authorization.ValidationInterpreter
import co.topl.node.transaction.{Attestations, IoTransaction, SpentTransactionOutput}
import co.topl.node.typeclasses.ContainsSignable.instances.ioTransactionSignable
import co.topl.quivr.Models.{Contextual, Primitive}
import co.topl.quivr.api.Verifier
import co.topl.quivr.runtime.DynamicContext
import co.topl.quivr.{Proof, Proposition, SignableBytes}
import co.topl.node.transaction.authorization.{ValidationErrors, ValidationError}

object Credentials {

  /**
   * Return a Proof (if possible) that will satisfy a Proposition and signable bytes
   *
   * It may not be possible to retrieve a proof if
   * - The proposition type is not yet supported (not one of Locked, Digest, Signature, Height and Tick)
   * - The secret data required for the proof is not available at idx
   *
   * @param msg Signable bytes to bind to the proof
   * @param proposition Proposition in which the Proof should satisfy
   * @param idx Indices for which the proof's secret data can be obtained from
   * @return The Proof (if possible)
   */
  private def getProof(msg: SignableBytes, proposition: Proposition, idx: Indices): Option[Proof] = {
    proposition match {
      case _: Primitive.Locked.Proposition => Some(
        QuivrService.lockedProof(msg)
      )
      case _: Primitive.Digest.Proposition =>
        Wallet.getPreimage(idx).map(QuivrService.digestProof(msg, _))
      case _: Primitive.DigitalSignature.Proposition =>
        Wallet.getKeyPair(idx).map(keyPair => QuivrService.signatureProof(msg, keyPair.sk))
      case _: Contextual.HeightRange.Proposition => Some(
        QuivrService.heightProof(msg)
      )
      case _: Contextual.TickRange.Proposition => Some(
        QuivrService.tickProof(msg)
      )
      case _ => None
    }
  }

  /***
   * Prove an input. That is, to prove all the propositions within the attestation
   *
   * If the wallet is unaware of the input's identifier, the input will remain unproven
   *
   * @param input Input to prove. Once proven, the input can be spent
   *              Although the input is not yet spent, it is of type SpentTransactionOutput to denote its state
   *              after the transaction is accepted into the blockchain.
   * @param msg signable bytes to bind to the proofs
   * @return The same input, but proven
   */
  private def proveInput(input: SpentTransactionOutput, msg: SignableBytes): SpentTransactionOutput =
    Wallet.getIndicesByIdentifier(input.knownIdentifier).map { idx =>
      val attestations = input.attestation match {
        case Attestations.Predicate(predLock, _) => Attestations.Predicate(
          predLock,
          predLock.challenges.map(prop => getProof(msg, prop, idx))
        )
        case _ => ??? // We are not handling other types of Attestations at this moment in time
      }

      SpentTransactionOutput(input.knownIdentifier, attestations, input.value, input.datum, input.opts)
    }.getOrElse(input)



  /**
   * Prove a transaction. That is, to prove all the inputs within the transaction
   * @param unprovenTx The unproven transaction to prove
   * @return The proven version of the input
   */
  def prove(unprovenTx: IoTransaction): IoTransaction = {
    val signable = ioTransactionSignable.signableBytes(unprovenTx)
    val provenInputs = unprovenTx.inputs.map(proveInput(_, signable))

    IoTransaction(provenInputs, unprovenTx.outputs, unprovenTx.datum)
  }

  /**
   * Validate whether the transaction is authorized. That is, all contained attestations are satisfied
   * @param tx Transaction to validate
   * @param ctx Context to validate the transaction in
   * @return Iff transaction is authorized
   */
  def validate(tx: IoTransaction)(implicit ctx: DynamicContext[Option, String]): Boolean = {
    implicit val verifier: Verifier[Option] = Verifier.instances.verifierInstance
    ValidationInterpreter
      .make[Option]()
      .validate(ctx)(tx)
      .exists(_.isRight)
  }

  /**
   * Prove a transaction. That is, to prove all the inputs within the transaction
   *
   * @param unprovenTx The unproven transaction to prove
   * @return The proven version of the input
   */
  def proveAndValidate(unprovenTx: IoTransaction)(implicit ctx: DynamicContext[Option, String]): Either[ValidationError, IoTransaction] = {
    val signable = ioTransactionSignable.signableBytes(unprovenTx)
    val inputs = unprovenTx.inputs.map(proveInput(_, signable))

    val tx = IoTransaction(inputs, unprovenTx.outputs, unprovenTx.datum)
    if(validate(tx))
      Right(tx)
    else Left(ValidationErrors.ValidationFailed)
  }
}
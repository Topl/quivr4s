package co.topl.brambl.wallet

import co.topl.brambl.Models.Indices
import co.topl.brambl.wallet.CredentiallerErrors.{ProverError, ValidationError}
import co.topl.brambl.{Context, QuivrService}
import co.topl.node.transaction.authorization.ValidationInterpreter
import co.topl.node.transaction.{Attestation, Attestations, IoTransaction, SpentTransactionOutput}
import co.topl.node.typeclasses.ContainsSignable.instances.ioTransactionSignable
import co.topl.quivr.Models.{Contextual, Primitive}
import co.topl.quivr.api.Verifier
import co.topl.quivr.{Proof, Proposition, SignableBytes}

case class Credentialler(store: Storage)(implicit ctx: Context) extends Credentials {

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
  private def getProof(msg: SignableBytes, proposition: Proposition, idx: Option[Indices]): Option[Proof] = {
    proposition match {
      case _: Primitive.Locked.Proposition => QuivrService.lockedProof(msg)
      case _: Primitive.Digest.Proposition =>
        idx.flatMap(store.getPreimage(_).flatMap(QuivrService.digestProof(msg, _)))
      case p: Primitive.DigitalSignature.Proposition =>
        ctx.signingRoutines
          .get(p.routine)
          .flatMap(r =>
            idx
              .flatMap(i => store.getKeyPair(i, r))
              .flatMap(keyPair => QuivrService.signatureProof(msg, keyPair.sk, r))
          )
      case _: Contextual.HeightRange.Proposition => QuivrService.heightProof(msg)
      case _: Contextual.TickRange.Proposition => QuivrService.tickProof(msg)
      case _ => None
    }
  }

  /***
   * Prove an input. That is, to prove all the propositions within the attestation
   *
   * If the wallet is unaware of the input's identifier, an error is returned
   *
   * @param input Input to prove. Once proven, the input can be spent
   *              Although the input is not yet spent, it is of type SpentTransactionOutput to denote its state
   *              after the transaction is accepted into the blockchain.
   * @param msg signable bytes to bind to the proofs
   * @return The same input, but proven. If the input is unprovable, an error is returned.
   */
  private def proveInput(input: SpentTransactionOutput, msg: SignableBytes): Either[ProverError, SpentTransactionOutput] = {
    val idx: Option[Indices] = store.getIndicesByIdentifier(input.knownIdentifier)
    val attestations: Either[ProverError, Attestation] = input.attestation match {
      case Attestations.Predicate(predLock, responses) => {
        if(predLock.challenges.length != responses.length) Left(CredentiallerErrors.AttestationMalformed(input.attestation))
        else Right(
          Attestations.Predicate(predLock, predLock.challenges.map(getProof(msg, _, idx)))
        )
      }

      case _ => ??? // We are not handling other types of Attestations at this moment in time
    }

    attestations.map(SpentTransactionOutput(input.knownIdentifier, _, input.value, input.datum, input.opts))
  }


  /**
   * Prove a transaction. That is, prove all the inputs within the transaction if possible
   *
   * If not possible, errors for the unprovable inputs are returned
   *
   * @param unprovenTx The unproven transaction to prove
   * @return The proven version of the transaction. If not possible, errors for the unprovable inputs are returned
   */
  override def prove(unprovenTx: IoTransaction): Either[List[ProverError], IoTransaction] = {
    val signable = ioTransactionSignable.signableBytes(unprovenTx)
    val (errs, provenInputs) = unprovenTx.inputs
      .partitionMap(proveInput(_, signable))

    if(errs.isEmpty && provenInputs.nonEmpty) Right(IoTransaction(provenInputs, unprovenTx.outputs, unprovenTx.datum))
    else Left(errs)
  }

  /**
   * Validate whether the transaction is authorized. That is, all contained attestations are satisfied
   * @param tx Transaction to validate
   * @param ctx Context to validate the transaction in
   * @return Iff transaction is authorized
   */
  override def validate(tx: IoTransaction): List[ValidationError] = {
    implicit val verifier: Verifier[Option] = Verifier.instances.verifierInstance
    ValidationInterpreter
      .make[Option]()
      .validate(ctx)(tx)
      .flatMap({
        case Left(err) => Some(ValidationError(err))
        case _ => None
      })
      .toList
  }

  /**
   * Prove and validate a transaction.
   * That is, attempt to prove all the inputs within the transaction and validate if the transaction is successfully proven
   *
   * @param unprovenTx The unproven transaction to prove
   * @return The proven version of the input is successfully proven. Else a validation error
   */
  override def proveAndValidate(unprovenTx: IoTransaction): Either[List[CredentiallerError], IoTransaction] =
    prove(unprovenTx) match {
      case Right(provenTx) => validate(provenTx) match {
        case Nil => Right(provenTx)
        case errs: List[ValidationError] => Left(errs)
      }
      case Left(errs) => Left(errs)
    }
}
package co.topl.node.transaction.authorization

import cats.Monad
import cats.implicits._
import co.topl.node.Identifiers
import co.topl.node.transaction.{Attestations, IoTransaction}
import co.topl.quivr.api.Verifier
import co.topl.quivr.runtime.DynamicContext
import co.topl.quivr.{Proof, Proposition}

/**
 * Validates that each Input within a Transaction is properly "authorized".  "Authorized" simply means "does the given
 * Proof satisfy the given Proposition?".
 */
object ValidationInterpreter {

  def make[F[_]: Monad: Verifier](): ValidationAlgebra[F] =
    new ValidationAlgebra[F] {

      /**
       * Verifies each (Proposition, Proof) pair in the given Transaction
       */
      override def validate(context: DynamicContext[F, String])(
        transaction:                 IoTransaction
      ): F[Either[ValidationError, IoTransaction]] =
        transaction.inputs.zipWithIndex
          .foldLeft(Either.right[ValidationError, IoTransaction](transaction).pure[F]) { case (acc, (input, index)) =>
            input.attestation match {
              case Attestations.Predicate(lock, responses) =>
                predicateValidate(lock.challenges, lock.threshold, responses, context).map(r => r.map(_ => transaction))

              case Attestations.Image32(lock, known, responses) =>
                image32Validate(lock.leaves, lock.threshold, known, responses, context).map(r =>
                  r.map(_ => transaction)
                )

              case Attestations.Image64(lock, known, responses) =>
                image64Validate(lock.leaves, lock.threshold, known, responses, context).map(r =>
                  r.map(_ => transaction)
                )

              case Attestations.Commitment32(lock, known, responses) =>
                commitment32Validate(lock.root, lock.threshold, known, responses, context).map(r =>
                  r.map(_ => transaction)
                )

              case Attestations.Commitment64(lock, known, responses) =>
                commitment64Validate(lock.root, lock.threshold, known, responses, context).map(r =>
                  r.map(_ => transaction)
                )
            }
          }

      private def predicateValidate(
        challenges: List[Proposition],
        threshold:  Int,
        responses:  List[Option[Proof]],
        context:    DynamicContext[F, String]
      ): F[Either[ValidationError, Boolean]] =
        thresholdVerifier(challenges.map(Some(_)), responses, threshold, context)

      private def image32Validate(
        leaves:    List[Identifiers.Lock32],
        threshold: Int,
        known:     List[Option[Proposition]],
        responses: List[Option[Proof]],
        context:   DynamicContext[F, String]
      ): F[Either[ValidationError, Boolean]] =
        // check that the known Propositions match the leaves?
        thresholdVerifier(known, responses, threshold, context)

      private def image64Validate(
        leaves:    List[Identifiers.Lock64],
        threshold: Int,
        known:     List[Option[Proposition]],
        responses: List[Option[Proof]],
        context:   DynamicContext[F, String]
      ): F[Either[ValidationError, Boolean]] =
        thresholdVerifier(known, responses, threshold, context)

      // commitments need an additional proof of membership to be provided with the proposition
      private def commitment32Validate(
        root:      Identifiers.AccumulatorRoot32,
        threshold: Int,
        known:     List[Option[Proposition]],
        responses: List[Option[Proof]],
        context:   DynamicContext[F, String]
      ): F[Either[ValidationError, Boolean]] =
        thresholdVerifier(known, responses, threshold, context)

      private def commitment64Validate(
        root:      Identifiers.AccumulatorRoot64,
        threshold: Int,
        known:     List[Option[Proposition]],
        responses: List[Option[Proof]],
        context:   DynamicContext[F, String]
      ): F[Either[ValidationError, Boolean]] =
        thresholdVerifier(known, responses, threshold, context)

      private def thresholdVerifier(
        propositions:      List[Option[Proposition]],
        proofs:            List[Option[Proof]],
        threshold:         Int,
        context:           DynamicContext[F, String]
      )(implicit verifier: Verifier[F]): F[Either[ValidationError, Boolean]] = for {
        evalAuth <-
          if (threshold === 0) true.pure[F]
          else if (threshold >= propositions.size) false.pure[F]
          else if (proofs.isEmpty) false.pure[F]
          // We assume a one-to-one pairing of sub-proposition to sub-proof with the assumption that some of the proofs
          // may be Proofs.False
          else if (proofs.size =!= propositions.size) false.pure[F]
          else {
            propositions
              .zip(proofs)
              .foldLeftM(0L) {
                case (successCount, _) if successCount >= threshold =>
                  successCount.pure[F]
                case (successCount, (_, None)) =>
                  successCount.pure[F]
                case (successCount, (Some(prop: Proposition), Some(proof: Proof))) =>
                  verifier.evaluate(prop, proof, context).map {
                    case Right(true) => successCount + 1
                    case _           => successCount
                  }
              }
              .map(_ >= threshold)
          }
        res <- Either.cond(evalAuth, evalAuth, ValidationErrors.ValidationFailed).pure[F]
      } yield res
    }
}

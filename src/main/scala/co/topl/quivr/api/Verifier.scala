package co.topl.quivr.api

import cats._
import cats.implicits._
import co.topl.common.Models.{DigestVerification, Message, SignatureVerification}
import co.topl.crypto.hash.blake2b256
import co.topl.quivr._
import co.topl.quivr.runtime.QuivrRuntimeErrors.ValidationError.{
  EvaluationAuthorizationFailed,
  LockedPropositionIsUnsatisfiable,
  MessageAuthorizationFailed
}
import co.topl.quivr.runtime.{DynamicContext, QuivrRuntimeError}

import java.nio.charset.StandardCharsets

/**
 * A Verifier evaluates whether a given Proof satisfies a certain Proposition
 */
trait Verifier[F[_]] {

  /**
   * Does the given `proof` satisfy the given `proposition` using the given `data`?
   */
  def evaluate[C <: Proposition, R <: Proof](
    proposition: C,
    proof:       R,
    context:     DynamicContext[F, String]
  ): F[Either[QuivrRuntimeError, Boolean]]
}

object Verifier {

  /**
   * @param tag     an identifier of the Operation
   * @param context the Dynamic evaluation context which should provide an API for retrieving the signable bytes
   * @return an array of bytes that is similar to a "signature" for the proof
   */
  private def evaluateBlake2b256Bind[F[_]: Monad, A](
    tag:     String,
    proof:   Proof,
    context: DynamicContext[F, A]
  ): F[Boolean] = for {
    sb             <- context.signableBytes
    verifierTxBind <- blake2b256.hash(tag.getBytes(StandardCharsets.UTF_8) ++ sb).value.pure[F]
    msgAuth = verifierTxBind.sameElements(proof.bindToTransaction)
  } yield msgAuth

  trait Implicits {

    implicit class PropositionOps(proposition: Proposition) {

      def isSatisfiedBy[F[_]](
        proof:            Proof
      )(implicit context: DynamicContext[F, String], ev: Verifier[F]): F[Either[QuivrRuntimeError, Boolean]] =
        ev.evaluate(proposition, proof, context)
    }

    implicit class ProofOps(proof: Proof) {

      def satisfies[F[_]](
        proposition: Proposition
      )(implicit ev: Verifier[F], context: DynamicContext[F, String]): F[Either[QuivrRuntimeError, Boolean]] =
        ev.evaluate(proposition, proof, context)
    }
  }

  object implicits extends Implicits

  trait Instances {

    private def collectResult(proposition: Proposition, proof: Proof)(
      msgResult:                           Boolean,
      evalResult:                          Either[QuivrRuntimeError, _]
    ): Either[QuivrRuntimeError, Boolean] =
      (msgResult, evalResult) match {
        case (true, Right(_)) => Right[QuivrRuntimeError, Boolean](true)
        case (true, Left(e))  => Left[QuivrRuntimeError, Boolean](e)
        case (false, _)       => Left(MessageAuthorizationFailed(proof))
      }

    private def lockedVerifier[F[_]: Monad](
      proposition: Models.Primitive.Locked.Proposition,
      proof:       Models.Primitive.Locked.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[QuivrRuntimeError, Boolean]] =
      // should always fail, the Locked Proposition is unsatisfiable
      Either
        .left[QuivrRuntimeError, Boolean](LockedPropositionIsUnsatisfiable)
        .pure[F]

    private def digestVerifier[F[_]: Monad](
      proposition: Models.Primitive.Digest.Proposition,
      proof:       Models.Primitive.Digest.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[QuivrRuntimeError, Boolean]] = for {
      msgResult <- Verifier.evaluateBlake2b256Bind(Models.Primitive.Digest.token, proof, context)
      verification = DigestVerification(proposition.digest, proof.preimage)
      evalResult <- context.digestVerify(proposition.routine)(verification).value
      res = collectResult(proposition, proof)(msgResult, evalResult)
    } yield res

    private def signatureVerifier[F[_]: Monad](
      proposition: Models.Primitive.DigitalSignature.Proposition,
      proof:       Models.Primitive.DigitalSignature.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[QuivrRuntimeError, Boolean]] = for {
      msgResult     <- Verifier.evaluateBlake2b256Bind(Models.Primitive.DigitalSignature.token, proof, context)
      signedMessage <- context.signableBytes
      verification = SignatureVerification(proposition.vk, proof.witness, Message(signedMessage))
      evalResult <- context.signatureVerify(proposition.routine)(verification).value
      res = collectResult(proposition, proof)(msgResult, evalResult)
    } yield res

    private def heightVerifier[F[_]: Monad](
      proposition: Models.Contextual.HeightRange.Proposition,
      proof:       Models.Contextual.HeightRange.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[QuivrRuntimeError, Boolean]] = for {
      msgResult   <- Verifier.evaluateBlake2b256Bind(Models.Contextual.HeightRange.token, proof, context)
      chainHeight <- context.heightOf(proposition.chain)
      evalResult = chainHeight.map(h => proposition.min <= h && h <= proposition.max)
      res = collectResult(proposition, proof)(msgResult, evalResult)
    } yield res

    private def tickVerifier[F[_]: Monad](
      proposition: Models.Contextual.TickRange.Proposition,
      proof:       Models.Contextual.TickRange.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[QuivrRuntimeError, Boolean]] = for {
      msgResult  <- Verifier.evaluateBlake2b256Bind(Models.Contextual.TickRange.token, proof, context)
      evalResult <- context.currentTick.map(t => Right(proposition.min <= t && t <= proposition.max))
      res = collectResult(proposition, proof)(msgResult, evalResult)
    } yield res

    private def exactMatchVerifier[F[_]: Monad](
      proposition: Models.Contextual.ExactMatch.Proposition,
      proof:       Models.Contextual.ExactMatch.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[QuivrRuntimeError, Boolean]] = for {
      msgResult  <- Verifier.evaluateBlake2b256Bind(Models.Contextual.ExactMatch.token, proof, context)
      evalResult <- context.exactMatch(proposition.label, proposition.compareTo).value
      res = collectResult(proposition, proof)(msgResult, evalResult)
    } yield res

    private def lessThanVerifier[F[_]: Monad](
      proposition: Models.Contextual.LessThan.Proposition,
      proof:       Models.Contextual.LessThan.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[QuivrRuntimeError, Boolean]] = for {
      msgResult  <- Verifier.evaluateBlake2b256Bind(Models.Contextual.LessThan.token, proof, context)
      evalResult <- context.lessThan(proposition.label, proposition.compareTo).value
      res = collectResult(proposition, proof)(msgResult, evalResult)
    } yield res

    private def greaterThanVerifier[F[_]: Monad](
      proposition: Models.Contextual.GreaterThan.Proposition,
      proof:       Models.Contextual.GreaterThan.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[QuivrRuntimeError, Boolean]] = for {
      msgResult  <- Verifier.evaluateBlake2b256Bind(Models.Contextual.GreaterThan.token, proof, context)
      evalResult <- context.greaterThan(proposition.label, proposition.compareTo).value
      res = collectResult(proposition, proof)(msgResult, evalResult)
    } yield res

    private def equalToVerifier[F[_]: Monad](
      proposition: Models.Contextual.EqualTo.Proposition,
      proof:       Models.Contextual.EqualTo.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[QuivrRuntimeError, Boolean]] = for {
      msgResult  <- Verifier.evaluateBlake2b256Bind(Models.Contextual.EqualTo.token, proof, context)
      evalResult <- context.equalTo(proposition.label, proposition.compareTo).value
      res = collectResult(proposition, proof)(msgResult, evalResult)
    } yield res

    private def thresholdVerifier[F[_]: Monad](
      proposition:       Models.Compositional.Threshold.Proposition,
      proof:             Models.Compositional.Threshold.Proof,
      context:           DynamicContext[F, String]
    )(implicit verifier: Verifier[F]): F[Either[QuivrRuntimeError, Boolean]] =
      for {
        msgResult <- Verifier.evaluateBlake2b256Bind(Models.Compositional.Threshold.token, proof, context)
        evalResult <-
          if (proposition.threshold === 0) true.pure[F]
          else if (proposition.threshold >= proposition.challenges.size) false.pure[F]
          else if (proof.responses.isEmpty) false.pure[F]
          // We assume a one-to-one pairing of sub-proposition to sub-proof with the assumption that some of the proofs
          // may be Proofs.False
          else if (proof.responses.size =!= proposition.challenges.size) false.pure[F]
          else {
            proposition.challenges.toList
              .zip(proof.responses)
              .foldLeftM(0L) {
                case (successCount, _) if successCount >= proposition.threshold =>
                  successCount.pure[F]
                case (successCount, (_, None)) =>
                  successCount.pure[F]
                case (successCount, (prop: Proposition, Some(proof: Proof))) =>
                  verifier.evaluate(prop, proof, context).map {
                    case Right(true) => successCount + 1
                    case _           => successCount
                  }
              }
              .map(_ >= proposition.threshold)
          }
        res = (msgResult, evalResult) match {
          case (true, true)  => Right[QuivrRuntimeError, Boolean](true)
          case (true, false) => Left[QuivrRuntimeError, Boolean](EvaluationAuthorizationFailed(proposition, proof))
          case (false, _)    => Left(MessageAuthorizationFailed(proof))
        }
      } yield res

    private def notVerifier[F[_]: Monad](
      proposition: Models.Compositional.Not.Proposition,
      proof:       Models.Compositional.Not.Proof,
      context:     DynamicContext[F, String]
    )(implicit
      verifier: Verifier[F]
    ): F[Either[QuivrRuntimeError, Boolean]] = for {
      msgResult  <- Verifier.evaluateBlake2b256Bind(Models.Compositional.Not.token, proof, context)
      evalResult <- verifier.evaluate(proposition.proposition, proof.proof, context)
      res = collectResult(proposition, proof)(msgResult, evalResult)
    } yield res

    private def andVerifier[F[_]: Monad](
      proposition: Models.Compositional.And.Proposition,
      proof:       Models.Compositional.And.Proof,
      context:     DynamicContext[F, String]
    )(implicit
      verifier: Verifier[F]
    ): F[Either[QuivrRuntimeError, Boolean]] = for {
      msgResult <- Verifier.evaluateBlake2b256Bind(Models.Compositional.And.token, proof, context)
      aResult   <- verifier.evaluate(proposition.left, proof.left, context)
      bResult   <- verifier.evaluate(proposition.right, proof.right, context)
      res = (msgResult, aResult, bResult) match {
        case (true, Right(_), Right(_)) => Right[QuivrRuntimeError, Boolean](true)
        case (true, aError, Right(_))   => aError
        case (true, Right(_), bError)   => bError
        case (false, _, _)              => Left(MessageAuthorizationFailed(proof))
      }
    } yield res

    private def orVerifier[F[_]: Monad](
      proposition: Models.Compositional.Or.Proposition,
      proof:       Models.Compositional.Or.Proof,
      context:     DynamicContext[F, String]
    )(implicit
      verifier: Verifier[F]
    ): F[Either[QuivrRuntimeError, Boolean]] = for {
      msgResult <- Verifier.evaluateBlake2b256Bind(Models.Compositional.Or.token, proof, context)
      aResult   <- verifier.evaluate(proposition.left, proof.left, context)
      bResult   <- verifier.evaluate(proposition.right, proof.right, context)
      res = (msgResult, aResult, bResult) match {
        case (true, Right(_), _)      => Right[QuivrRuntimeError, Boolean](true)
        case (true, _, Right(_))      => Right[QuivrRuntimeError, Boolean](true)
        case (true, aError, Right(_)) => aError
        case (true, Right(_), bError) => bError
        case (false, _, _)            => Left(MessageAuthorizationFailed(proof))
      }
    } yield res

    implicit def verifierInstance[F[_]: Monad]: Verifier[F] =
      new Verifier[F] {

        def evaluate[C <: Proposition, R <: Proof](
          proposition: C,
          proof:       R,
          context:     DynamicContext[F, String]
        ): F[Either[QuivrRuntimeError, Boolean]] =
          (proposition, proof) match {
            case (c: Models.Primitive.Locked.Proposition, r: Models.Primitive.Locked.Proof) =>
              lockedVerifier(c, r, context)
            case (c: Models.Primitive.Digest.Proposition, r: Models.Primitive.Digest.Proof) =>
              digestVerifier(c, r, context)
            case (c: Models.Primitive.DigitalSignature.Proposition, r: Models.Primitive.DigitalSignature.Proof) =>
              signatureVerifier(c, r, context)
            case (c: Models.Contextual.HeightRange.Proposition, r: Models.Contextual.HeightRange.Proof) =>
              heightVerifier(c, r, context)
            case (c: Models.Contextual.TickRange.Proposition, r: Models.Contextual.TickRange.Proof) =>
              tickVerifier(c, r, context)
            case (c: Models.Contextual.ExactMatch.Proposition, r: Models.Contextual.ExactMatch.Proof) =>
              exactMatchVerifier(c, r, context)
            case (c: Models.Contextual.LessThan.Proposition, r: Models.Contextual.LessThan.Proof) =>
              lessThanVerifier(c, r, context)
            case (c: Models.Contextual.GreaterThan.Proposition, r: Models.Contextual.GreaterThan.Proof) =>
              greaterThanVerifier(c, r, context)
            case (c: Models.Contextual.EqualTo.Proposition, r: Models.Contextual.EqualTo.Proof) =>
              equalToVerifier(c, r, context)
            case (c: Models.Compositional.Threshold.Proposition, r: Models.Compositional.Threshold.Proof) =>
              implicit def v: Verifier[F] = verifierInstance[F]
              thresholdVerifier(c, r, context)
            case (c: Models.Compositional.Not.Proposition, r: Models.Compositional.Not.Proof) =>
              implicit def v: Verifier[F] = verifierInstance[F]
              notVerifier(c, r, context)
            case (c: Models.Compositional.And.Proposition, r: Models.Compositional.And.Proof) =>
              implicit def v: Verifier[F] = verifierInstance[F]
              andVerifier(c, r, context)
            case (c: Models.Compositional.Or.Proposition, r: Models.Compositional.Or.Proof) =>
              implicit def v: Verifier[F] = verifierInstance[F]
              orVerifier(c, r, context)
          }
      }
  }

  object instances extends Instances
}

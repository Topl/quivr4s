package co.topl.quivr

import cats._
import cats.data.EitherT
import cats.implicits._
import co.topl.crypto.hash.blake2b256
import co.topl.quivr.runtime.DynamicContext
import co.topl.quivr.runtime.Errors.AuthorizationErrors.{EvaluationAuthorizationFailed, LockedPropositionIsUnsatisfiable}
import co.topl.quivr.runtime.Errors.SyntaxErrors
import co.topl.quivr.runtime.Errors.SyntaxErrors.MessageAuthorizationFailed
import co.topl.common.{DigestVerification, SignatureVerification, Message}

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
  ): F[Boolean]
}

object Verifier {

  /**
   * @param tag     an identifier of the Operation
   * @param context the Dynamic evaluation context which should provide an API for retrieving the signable bytes
   * @return an array of bytes that is similar to a "signature" for the proof
   */
  def evaluateBind[F[_]: Monad, A](tag: Byte, proof: Proof, context: DynamicContext[F, A])(
    f:                                  Array[Byte] => F[TxBind]
  ): F[Either[SyntaxErrors.MessageAuthorizationFailed.type, SignableTxBytes]] = for {
    sb             <- context.signableBytes
    verifierTxBind <- f(sb :+ tag)
    msgAuth = Either.cond(verifierTxBind.sameElements(proof.bindToTransaction), sb, MessageAuthorizationFailed)
  } yield msgAuth

  protected def bindFunc[F[_]: Applicative](m: Array[Byte]): F[Array[Byte]] = blake2b256.hash(m).value.pure[F]

  trait Implicits {

    implicit class PropositionOps(proposition: Proposition) {

      def isSatisfiedBy[F[_]](
        proof:            Proof
      )(implicit context: DynamicContext[F, String], ev: Verifier[F]): F[Boolean] =
        ev.evaluate(proposition, proof, context)
    }

    implicit class ProofOps(proof: Proof) {

      def satisfies[F[_]](
        proposition: Proposition
      )(implicit ev: Verifier[F], context: DynamicContext[F, String]): F[Boolean] =
        ev.evaluate(proposition, proof, context)
    }
  }

  object implicits extends Implicits

  trait Instances {

    private def lockedVerifier[F[_]: Monad](
      proposition: Models.Primitive.Locked.Proposition,
      proof:       Models.Primitive.Locked.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[runtime.Error, Boolean]] = for {
      msgResult   <- Verifier.evaluateBind(Models.Primitive.Digest.token, proof, context)(Verifier.bindFunc[F])
      res = Either.left[runtime.Error, Boolean](LockedPropositionIsUnsatisfiable)
    } yield res // should always fail, the Locked Proposition is unsatisfiable

    private def digestVerifier[F[_]: Monad](
      proposition: Models.Primitive.Digest.Proposition,
      proof:       Models.Primitive.Digest.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[runtime.Error, DigestVerification]] = for {
      msgResult   <- Verifier.evaluateBind(Models.Primitive.Digest.token, proof, context)(Verifier.bindFunc[F])
      verification = DigestVerification(proposition.digest, proof.preimage, proof.salt)
      evalResult <- context.digestVerify(proposition.routine)(verification).value
      res = Either
        .cond(
          msgResult.isRight && evalResult.isRight,
          verification,
          EvaluationAuthorizationFailed(proposition, proof)
        )
    } yield res

    private def signatureVerifier[F[_]: Monad](
      proposition: Models.Primitive.DigitalSignature.Proposition,
      proof:       Models.Primitive.DigitalSignature.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[runtime.Error, SignatureVerification]] = for {
      msgResult   <- Verifier.evaluateBind(Models.Primitive.DigitalSignature.token, proof, context)(Verifier.bindFunc[F])
      signedMessage <- context.signableBytes
      verification = SignatureVerification(proposition.vk, proof.witness, Message(signedMessage))
      evalResult <- context.signatureVerify(proposition.routine)(verification).value
      res = Either
        .cond(
          msgResult.isRight && evalResult.isRight,
          verification,
          EvaluationAuthorizationFailed(proposition, proof)
        )
    } yield res

    private def heightVerifier[F[_]: Monad](
      proposition: Models.Contextual.HeightRange.Proposition,
      proof:       Models.Contextual.HeightRange.Proof,
      context:     DynamicContext[F, String]
    ): F[Either[runtime.Error, Long]] = for {
      msgResult <- Verifier.evaluateBind(Models.Contextual.HeightRange.token, proof, context)(Verifier.bindFunc[F])
      height <- context.heightOf(proposition.chain)
      // evalResult <- Either
      //   .cond(
      //     proposition.min <= height && height <= proposition.max,
      //     height,
      //     EvaluationAut.horizationFailed(proposition, proof)
      //   )
      // res = Either
      //   .cond(
      //     msgResult.isRight && evalResult.isRight
      //     verification,
      //     EvaluationAuthorizationFailed(proposition, proof)
      //   )
    } yield res

    // private def tickVerifier[F[_]: Monad](
    //   proposition: Models.Contextual.TickRange.Proposition,
    //   proof:       Models.Contextual.TickRange.Proof,
    //   context:     DynamicContext[F, String]
    // ): F[Either[runtime.Error, Long]] = for {
    //   msgAuth <- Verifier.evaluateBind(Models.Contextual.TickRange.token, proof, context)(
    //     blake2b256.hash(_).value.pure[F]
    //   )
    //   currentTick <- context.currentTick
    //   evalAuth = proposition.min <= currentTick && currentTick <= proposition.max
    //   res = msgAuth && evalAuth
    // } yield res

    // private def exactMatchVerifier[F[_]: Monad](
    //   proposition: Models.Contextual.ExactMatch.Proposition,
    //   proof:       Models.Contextual.ExactMatch.Proof,
    //   context:     DynamicContext[F, String]
    // ): F[Either[runtime.Error, Array[Byte]]] = for {
    //   msgAuth <- Verifier.evaluateBind(Models.Contextual.ExactMatch.token, proof, context)(
    //     blake2b256.hash(_).value.pure[F]
    //   )
    //   evalAuth = context.exactMatch(proposition.label, proposition.compareTo)
    //   res = msgAuth && evalAuth
    // } yield res

    // private def lessThanVerifier[F[_]: Monad](
    //   proposition: Models.Contextual.LessThan.Proposition,
    //   proof:       Models.Contextual.LessThan.Proof,
    //   context:     DynamicContext[F, String]
    // ): F[Either[runtime.Error, Long]] = for {
    //   msgAuth <- Verifier.evaluateBind(Models.Contextual.LessThan.token, proof, context)(
    //     blake2b256.hash(_).value.pure[F]
    //   )
    //   evalAuth = context.lessThan(proposition.label, proposition.compareTo)
    //   res = msgAuth && evalAuth
    // } yield res

    // private def greaterThanVerifier[F[_]: Monad](
    //   proposition: Models.Contextual.GreaterThan.Proposition,
    //   proof:       Models.Contextual.GreaterThan.Proof,
    //   context:     DynamicContext[F, String]
    // ): F[Either[runtime.Error, Long]] = for {
    //   msgAuth <- Verifier.evaluateBind(Models.Contextual.GreaterThan.token, proof, context)(
    //     blake2b256.hash(_).value.pure[F]
    //   )
    //   evalAuth = context.greaterThan(proposition.label, proposition.compareTo)
    //   res = msgAuth && evalAuth
    // } yield res

    // private def equalToVerifier[F[_]: Monad](
    //   proposition: Models.Contextual.EqualTo.Proposition,
    //   proof:       Models.Contextual.EqualTo.Proof,
    //   context:     DynamicContext[F, String]
    // ): F[Either[runtime.Error, Long]] = for {
    //   msgAuth <- Verifier.evaluateBind(Models.Contextual.EqualTo.token, proof, context)(
    //     blake2b256.hash(_).value.pure[F]
    //   )
    //   evalAuth = context.equalTo(proposition.label, proposition.compareTo)
    //   res = msgAuth && evalAuth
    // } yield res

    private def thresholdVerifier[F[_]: Monad](
      proposition:       Models.Compositional.Threshold.Proposition,
      proof:             Models.Compositional.Threshold.Proof,
      context:           DynamicContext[F, String]
    )(implicit verifier: Verifier[F]): F[Either[runtime.Error, Boolean]] =
      for {
        msgAuth <- Verifier.evaluateBind(Models.Compositional.Threshold.token, proof, context)(
          blake2b256.hash(_).value.pure[F]
        )
        evalAuth <-
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
                    case true => successCount + 1
                    case _    => successCount
                  }
              }
              .map(_ >= proposition.threshold)
          }
        res = msgAuth && evalAuth
      } yield res

    private def notVerifier[F[_]: Monad](
      proposition: Models.Compositional.Not.Proposition,
      proof:       Models.Compositional.Not.Proof,
      context:     DynamicContext[F, String]
    )(implicit
      verifier: Verifier[F]
    ): F[Either[runtime.Error, Boolean]] = for {
      msgAuth <- Verifier.evaluateBind(Models.Compositional.Not.token, proof, context)(Verifier.bindFunc[F])
      internalAuth <- verifier.evaluate(proposition.proposition, proof.proof, context)
      evalAuth = !internalAuth
      res = msgAuth && evalAuth
    } yield res

    private def andVerifier[F[_]: Monad](
      proposition: Models.Compositional.And.Proposition,
      proof:       Models.Compositional.And.Proof,
      context:     DynamicContext[F, String]
    )(implicit
      verifier: Verifier[F]
    ): F[Either[runtime.Error, Boolean]] = for {
      msgAuth <- Verifier.evaluateBind(Models.Compositional.And.token, proof, context)(Verifier.bindFunc[F])
      internalAuth <- verifier.evaluate(proposition.left, proof.left, context)
      evalAuth <-
        if (internalAuth) {
          verifier.evaluate(proposition.right, proof.right, context)
        } else {
          false.pure[F]
        }
      res = msgAuth && evalAuth
    } yield res

    private def orVerifier[F[_]: Monad](
      proposition: Models.Compositional.Or.Proposition,
      proof:       Models.Compositional.Or.Proof,
      context:     DynamicContext[F, String]
    )(implicit
      verifier: Verifier[F]
    ): F[Either[runtime.Error, Boolean]] = for {
      msgAuth <- Verifier.evaluateBind(Models.Compositional.Or.token, proof, context)(Verifier.bindFunc[F])
      internalAuth <- verifier.evaluate(proposition.left, proof.left, context)
      evalAuth <-
        if (internalAuth) {
          true.pure[F]
        } else {
          verifier.evaluate(proposition.right, proof.right, context)
        }
      res = msgAuth && evalAuth
    } yield res

    implicit def verifierInstance[F[_]: Monad]: Verifier[F] =
      new Verifier[F] {

        override def evaluate[C <: Proposition, R <: Proof](
          proposition: C,
          proof:       R,
          context:     DynamicContext[F, String]
        ): F[Either[runtime.Error, Boolean]] =
          (proposition, proof) match {
            case (c: Models.Primitive.Locked.Proposition, r: Models.Primitive.Locked.Proof) =>
              lockedVerifier(c, r, context)
            case (c: Models.Primitive.Digest.Proposition, r: Models.Primitive.Digest.Proof) =>
              digestVerifier(c, r, context).map(_.)
            case (c: Models.Primitive.DigitalSignature.Proposition, r: Models.Primitive.DigitalSignature.Proof) =>
              signatureVerifier(c, r, context)
            case (c: Models.Contextual.HeightRange.Proposition, r: Models.Contextual.HeightRange.Proof) =>
              heightVerifier(c, r, context)
            case (c: Models.Contextual.TickRange.Proposition, r: Models.Contextual.TickRange.Proof) =>
              tickVerifier(c, r, context)
            // case (c: Models.Contextual.ExactMatch.Proposition, r: Models.Contextual.ExactMatch.Proof) =>
            //   exactMatchVerifier(c, r, context)
            // case (c: Models.Contextual.LessThan.Proposition, r: Models.Contextual.LessThan.Proof) =>
            //   lessThanVerifier(c, r, context)
            // case (c: Models.Contextual.GreaterThan.Proposition, r: Models.Contextual.GreaterThan.Proof) =>
            //   greaterThanVerifier(c, r, context)
            // case (c: Models.Contextual.EqualTo.Proposition, r: Models.Contextual.EqualTo.Proof) =>
            //   equalToVerifier(c, r, context)
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
            case _ =>
              false.pure[F]
          }
      }
  }

  object instances extends Instances
}

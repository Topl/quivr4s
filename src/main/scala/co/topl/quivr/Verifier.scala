package co.topl.quivr

import cats._
import cats.implicits._

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
    context:     Evaluation.DynamicContext[F]
  ): F[Boolean]
}

object Verifier {

  trait Implicits {

    implicit class PropositionOps(proposition: Proposition) {

      def isSatisfiedBy[F[_]](
        proof:            Proof
      )(implicit context: Evaluation.DynamicContext[F], ev: Verifier[F]): F[Boolean] =
        ev.evaluate(proposition, proof, context)
    }

    implicit class ProofOps(proof: Proof) {

      def satisfies[F[_]](
        proposition: Proposition
      )(implicit ev: Verifier[F], context: Evaluation.DynamicContext[F]): F[Boolean] =
        ev.evaluate(proposition, proof, context)
    }
  }

  object implicits extends Implicits

  trait Instances {

    private def lockedVerifier[F[_]: Monad](
      proposition: Models.Primitive.Locked.Proposition,
      proof:       Models.Primitive.Locked.Proof,
      context:     Evaluation.DynamicContext[F]
    ): F[Boolean] =
      // todo: consider optimizing by skipping the txBind verification? Perhaps we still want to know if the
      // prover bound to this value correctly though? I am unsure if this data will be recorded so not sure it
      // worth keeping around
      for {
        sb <- context.signableBytes
        verifierTxBind = Prover.bind(Models.Primitive.Locked.token, sb)
        msgAuth = verifierTxBind sameElements proof.transactionBind
        evalAuth = false // should always fail, the Locked Proposition is unsatisfiable
        res = msgAuth && evalAuth
      } yield res

    // todo: how to pass which of these failed back up?
    private def digestVerifier[F[_]: Monad](
      proposition: Models.Primitive.Digest.Proposition,
      proof:       Models.Primitive.Digest.Proof,
      context:     Evaluation.DynamicContext[F]
    ): F[Boolean] = for {
      sb <- context.signableBytes
      verifierTxBind = Prover.bind(Models.Primitive.Digest.token, sb)
      msgAuth = verifierTxBind sameElements proof.transactionBind
      evalAuth = context.digestVerify(proposition.routine)(proof.preimage, proposition.digest)
      res = msgAuth && evalAuth
    } yield res

    private def signatureVerifier[F[_]: Monad](
      proposition: Models.Primitive.DigitalSignature.Proposition,
      proof:       Models.Primitive.DigitalSignature.Proof,
      context:     Evaluation.DynamicContext[F]
    ): F[Boolean] = for {
      sb <- context.signableBytes
      verifierTxBind = Prover.bind(Models.Primitive.DigitalSignature.token, sb)
      msgAuth = verifierTxBind sameElements proof.transactionBind
      evalAuth = context.signatureVerify(proposition.routine)(proposition.vk, proof.witness, sb)
      res = msgAuth && evalAuth
    } yield res

    private def heightVerifier[F[_]: Monad](
      proposition: Models.Contextual.HeightRange.Proposition,
      proof:       Models.Contextual.HeightRange.Proof,
      context:     Evaluation.DynamicContext[F]
    ): F[Boolean] = for {
      sb <- context.signableBytes
      verifierTxBind = Prover.bind(Models.Contextual.HeightRange.token, sb)
      msgAuth = verifierTxBind sameElements proof.transactionBind
      height = context.heightOf(proposition.chain).fold(-1L)(identity)
      evalAuth = proposition.min <= height && height <= proposition.max
      res = msgAuth && evalAuth
    } yield res

    private def tickVerifier[F[_]: Monad](
      proposition: Models.Contextual.TickRange.Proposition,
      proof:       Models.Contextual.TickRange.Proof,
      context:     Evaluation.DynamicContext[F]
    ): F[Boolean] = for {
      sb <- context.signableBytes
      verifierTxBind = Prover.bind(Models.Contextual.TickRange.token, sb)
      msgAuth = verifierTxBind sameElements proof.transactionBind
      currentTick <- context.currentTick
      evalAuth = proposition.min <= currentTick && currentTick <= proposition.max
      res = msgAuth && evalAuth
    } yield res

    private def exactMatchVerifier[F[_]: Monad](
      proposition: Models.Contextual.ExactMatch.Proposition,
      proof:       Models.Contextual.ExactMatch.Proof,
      context:     Evaluation.DynamicContext[F]
    ): F[Boolean] = for {
      sb <- context.signableBytes
      verifierTxBind = Prover.bind(Models.Contextual.TickRange.token, sb)
      msgAuth = verifierTxBind sameElements proof.transactionBind
      evalAuth = context.exactMatch(proposition.label, proposition.compareTo)
      res = msgAuth && evalAuth
    } yield res

    private def lessThanVerifier[F[_]: Monad](
      proposition: Models.Contextual.LessThan.Proposition,
      proof:       Models.Contextual.LessThan.Proof,
      context:     Evaluation.DynamicContext[F]
    ): F[Boolean] = for {
      sb <- context.signableBytes
      verifierTxBind = Prover.bind(Models.Contextual.TickRange.token, sb)
      msgAuth = verifierTxBind sameElements proof.transactionBind
      evalAuth = context.lessThan(proposition.label, proposition.compareTo)
      res = msgAuth && evalAuth
    } yield res

    private def greaterThanVerifier[F[_]: Monad](
      proposition: Models.Contextual.GreaterThan.Proposition,
      proof:       Models.Contextual.GreaterThan.Proof,
      context:     Evaluation.DynamicContext[F]
    ): F[Boolean] = for {
      sb <- context.signableBytes
      verifierTxBind = Prover.bind(Models.Contextual.TickRange.token, sb)
      msgAuth = verifierTxBind sameElements proof.transactionBind
      evalAuth = context.greaterThan(proposition.label, proposition.compareTo)
      res = msgAuth && evalAuth
    } yield res

    private def equalToVerifier[F[_]: Monad](
      proposition: Models.Contextual.EqualTo.Proposition,
      proof:       Models.Contextual.EqualTo.Proof,
      context:     Evaluation.DynamicContext[F]
    ): F[Boolean] = for {
      sb <- context.signableBytes
      verifierTxBind = Prover.bind(Models.Contextual.TickRange.token, sb)
      msgAuth = verifierTxBind sameElements proof.transactionBind
      evalAuth = context.equalTo(proposition.label, proposition.compareTo)
      res = msgAuth && evalAuth
    } yield res

    private def thresholdVerifier[F[_]: Monad](
      proposition:       Models.Compositional.Threshold.Proposition,
      proof:             Models.Compositional.Threshold.Proof,
      context:           Evaluation.DynamicContext[F]
    )(implicit verifier: Verifier[F]): F[Boolean] =
      for {
        sb <- context.signableBytes
        verifierTxBind = Prover.bind(Models.Compositional.Threshold.token, sb)
        msgAuth = verifierTxBind sameElements proof.transactionBind
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
      context:     Evaluation.DynamicContext[F]
    )(implicit
      verifier: Verifier[F]
    ): F[Boolean] =
      verifier
        .evaluate(proposition.proposition, proof.proof, context)
        .map(!_)

    private def andVerifier[F[_]: Monad](
      proposition: Models.Compositional.And.Proposition,
      proof:       Models.Compositional.And.Proof,
      context:     Evaluation.DynamicContext[F]
    )(implicit
      verifier: Verifier[F]
    ): F[Boolean] =
      verifier
        .evaluate(proposition.left, proof.left, context)
        .flatMap {
          case true => verifier.evaluate(proposition.right, proof.right, context)
          case _    => false.pure[F]
        }

    private def orVerifier[F[_]: Monad](
      proposition: Models.Compositional.Or.Proposition,
      proof:       Models.Compositional.Or.Proof,
      context:     Evaluation.DynamicContext[F]
    )(implicit
      verifier: Verifier[F]
    ): F[Boolean] =
      verifier
        .evaluate(proposition.left, proof.left, context)
        .flatMap {
          case false => verifier.evaluate(proposition.right, proof.right, context)
          case _     => true.pure[F]
        }

    implicit def verifierInstance[F[_]: Monad]: Verifier[F] =
      new Verifier[F] {

        override def evaluate[C <: Proposition, R <: Proof](
          proposition: C,
          proof:       R,
          context:     Evaluation.DynamicContext[F]
        ): F[Boolean] =
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
            case _ =>
              false.pure[F]
          }
      }
  }

  object Instances extends Instances
}

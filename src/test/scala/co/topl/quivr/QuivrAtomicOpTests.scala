package co.topl.quivr

import cats.Id
import cats.Monad
import co.topl.common.Models
import co.topl.crypto.signatures.Curve25519
import co.topl.quivr.runtime.QuivrRuntimeErrors

/**
  * Set of tests for the Quivr Atomic Operations.
  */
class QuivrAtomicOpTests extends munit.FunSuite with MockHelpers {

  import co.topl.quivr.api.Proposer._
  
  import co.topl.quivr.api.Prover._
  
  import co.topl.quivr.api.Verifier.instances._

  implicit val applicativeId:Monad[Id] = cats.catsInstancesForId
  
    test("A locked proposition must return an LockedPropositionIsUnsatisfiable when evaluated") {
      val lockedProposition = LockedProposer.propose(None)
      val lockedProverProof = lockedProver.prove((), signableBytes)
      val result = verifierInstance.evaluate(lockedProposition, lockedProverProof, dynamicContext(lockedProposition, lockedProverProof))
      assertEquals(result.isLeft, true)
      assertEquals(result.left.toOption.collect({case QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable => true}).isDefined, true)
    }

    test("A tickProposer must evaluate to true when tick is in range") {
      val tickProposition = tickProposer.propose(900, 1000)
      val tickProverProof = tickProver.prove((), signableBytes)
      val result = verifierInstance.evaluate(tickProposition, tickProverProof, dynamicContext(tickProposition, tickProverProof))
      assertEquals(result.isRight, true)
      assertEquals(result.right.toOption.getOrElse(false), true)
    }

    test("A tickProposer must evaluate to false when tick is not in range") {
      val tickProposition = tickProposer.propose(1, 10)
      val tickProverProof = tickProver.prove((), signableBytes)
      val result = verifierInstance.evaluate(tickProposition, tickProverProof, dynamicContext(tickProposition, tickProverProof))
      assertEquals(result.isLeft, true)
      assertEquals(result.left.toOption.collect({case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) => true}).isDefined, true)
    }


    test("A heightProposer must evaluate to true when height is in range") {
      val heightProposition = heightProposer.propose("height", 900, 1000)
      val heightProverProof = heightProver.prove((), signableBytes)
      val result = verifierInstance.evaluate(heightProposition, heightProverProof, dynamicContext(heightProposition, heightProverProof))
      assertEquals(result.isRight, true)
      assertEquals(result.right.toOption.getOrElse(false), true)
    }

    test("A heightProposer must evaluate to false when height is not in range") {
      val heightProposition = heightProposer.propose("height", 1, 10)
      val heightProverProof = heightProver.prove((), signableBytes)
      val result = verifierInstance.evaluate(heightProposition, heightProverProof, dynamicContext(heightProposition, heightProverProof))
      assertEquals(result.isLeft, true)
      assertEquals(result.left.toOption.collect({case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) => true}).isDefined, true)
    }

    test("A signatureProposer must evaluate to true when the signature is correct") {
      val (sk, vk) = Curve25519.createKeyPair
      val signatureProposition = signatureProposer.propose("Curve25519", Models.VerificationKey(vk.value))
      val signature = Curve25519.sign(sk, signableBytes)
      val signatureProverProof = signatureProver.prove(Models.Witness(signature.value), signableBytes)
      val result = verifierInstance.evaluate(signatureProposition, signatureProverProof, dynamicContext(signatureProposition, signatureProverProof))
      assertEquals(result.isRight, true)
      assertEquals(result.right.toOption.getOrElse(false), true)
    }
    
    test("A signatureProposer must evaluate to false when the signature is not correct") {
      val (_, vk) = Curve25519.createKeyPair
      val (sk, _) = Curve25519.createKeyPair
      val signatureProposition = signatureProposer.propose("Curve25519", Models.VerificationKey(vk.value))
      val signature = Curve25519.sign(sk, signableBytes)
      val signatureProverProof = signatureProver.prove(Models.Witness(signature.value), signableBytes)
      val result = verifierInstance.evaluate(signatureProposition, signatureProverProof, dynamicContext(signatureProposition, signatureProverProof))
      assertEquals(result.isLeft, true)
      assertEquals(result.left.toOption.collect({case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) => true}).isDefined, true)
    }

  }
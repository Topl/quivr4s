package co.topl.quivr

import cats.Id
import cats.Monad
import co.topl.common.Models
import co.topl.crypto.signatures.Curve25519
import co.topl.quivr.runtime.QuivrRuntimeErrors

/**
  * Set of tests for the Quivr Composite Operations.
  *
  */
class QuivrCompositeOpsTests extends munit.FunSuite with MockHelpers {
  
  import co.topl.quivr.api.Proposer._
  
  import co.topl.quivr.api.Prover._
  
  import co.topl.quivr.api.Verifier.instances._

  implicit val applicativeId:Monad[Id] = cats.catsInstancesForId
  
    test("An and proposition must evaluate to true when both the verification of both proofs is true") {
      val (sk1, vk1) = Curve25519.createKeyPair
      val (sk2, vk2) = Curve25519.createKeyPair
      val signatureProposition1 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk1.value))
      val signatureProposition2 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk2.value))
      val andProposition = andProposer.propose(signatureProposition1, signatureProposition2)
      val signature1 = Curve25519.sign(sk1, signableBytes)
      val signature2 = Curve25519.sign(sk2, signableBytes)
      val signatureProverProof1 = signatureProver.prove(Models.Witness(signature1.value), signableBytes)
      val signatureProverProof2 = signatureProver.prove(Models.Witness(signature2.value), signableBytes)
      val andProverProof = andProver.prove((signatureProverProof1, signatureProverProof2), signableBytes)
      val result = verifierInstance.evaluate(andProposition, andProverProof, dynamicContext(andProposition, andProverProof))
      assertEquals(result.isRight, true)
    }
 
    test("An and proposition must evaluate to false when one of the proofs evaluates to false") {
      val (sk1, vk1) = Curve25519.createKeyPair
      val (_, vk2) = Curve25519.createKeyPair
      val (sk2, _) = Curve25519.createKeyPair
      val signatureProposition1 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk1.value))
      val signatureProposition2 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk2.value))
      val andProposition = andProposer.propose(signatureProposition1, signatureProposition2)
      val signature1 = Curve25519.sign(sk1, signableBytes)
      val signature2 = Curve25519.sign(sk2, signableBytes)
      val signatureProverProof1 = signatureProver.prove(Models.Witness(signature1.value), signableBytes)
      val signatureProverProof2 = signatureProver.prove(Models.Witness(signature2.value), signableBytes)
      val andProverProof = andProver.prove((signatureProverProof1, signatureProverProof2), signableBytes)
      val result = verifierInstance.evaluate(andProposition, andProverProof, dynamicContext(andProposition, andProverProof))
      assertEquals(result.isLeft, true)
      assertEquals(result.left.toOption.collect({case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) => true}).isDefined, true)
    }

    test("An or proposition must evaluate to true when one of the proofs evaluates to true") {
      val (sk1, vk1) = Curve25519.createKeyPair
      val (_, vk2) = Curve25519.createKeyPair
      val (sk2, _) = Curve25519.createKeyPair
      val signatureProposition1 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk1.value))
      val signatureProposition2 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk2.value))
      val orProposition = orProposer.propose(signatureProposition1, signatureProposition2)
      val signature1 = Curve25519.sign(sk1, signableBytes)
      val signature2 = Curve25519.sign(sk2, signableBytes)
      val signatureProverProof1 = signatureProver.prove(Models.Witness(signature1.value), signableBytes)
      val signatureProverProof2 = signatureProver.prove(Models.Witness(signature2.value), signableBytes)
      val orProverProof = orProver.prove((signatureProverProof1, signatureProverProof2), signableBytes)
      val result = verifierInstance.evaluate(orProposition, orProverProof, dynamicContext(orProposition, orProverProof))
      assertEquals(result.isRight, true)
    }
    
    test("An or proposition must evaluate to false when both proofs evaluate to false") {
      val (_, vk1) = Curve25519.createKeyPair
      val (sk1, _) = Curve25519.createKeyPair
      val (_, vk2) = Curve25519.createKeyPair
      val (sk2, _) = Curve25519.createKeyPair
      val signatureProposition1 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk1.value))
      val signatureProposition2 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk2.value))
      val orProposition = orProposer.propose(signatureProposition1, signatureProposition2)
      val signature1 = Curve25519.sign(sk1, signableBytes)
      val signature2 = Curve25519.sign(sk2, signableBytes)
      val signatureProverProof1 = signatureProver.prove(Models.Witness(signature1.value), signableBytes)
      val signatureProverProof2 = signatureProver.prove(Models.Witness(signature2.value), signableBytes)
      val orProverProof = orProver.prove((signatureProverProof1, signatureProverProof2), signableBytes)
      val result = verifierInstance.evaluate(orProposition, orProverProof, dynamicContext(orProposition, orProverProof))
      assertEquals(result.isLeft, true)
      assertEquals(result.left.toOption.collect({case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) => true}).isDefined, true)
    }

    test("A not proposition must evaluate to false when the proof in the parameter is true") {
      val heightProposition = heightProposer.propose("height", 900, 1000)
      val heightProverProof = heightProver.prove((), signableBytes)
      val notProposition = notProposer.propose(heightProposition)
      val notProverProof = notProver.prove(heightProverProof, signableBytes)
      val result = verifierInstance.evaluate(notProposition, notProverProof, dynamicContext(notProposition, notProverProof))
      assertEquals(result.isLeft, true)
      assertEquals(result.left.toOption.collect({case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) => true}).isDefined, true)
    }

    test("A not proposition must evaluate to true when the proof in the parameter is false") {
      val heightProposition = heightProposer.propose("height", 1, 10)
      val heightProverProof = heightProver.prove((), signableBytes)
      val notProposition = notProposer.propose(heightProposition)
      val notProverProof = notProver.prove(heightProverProof, signableBytes)
      val result = verifierInstance.evaluate(notProposition, notProverProof, dynamicContext(notProposition, notProverProof))
      assertEquals(result.isRight, true)
    }

    test("A threshold proposition must evaluate to true when the threshold is passed") {
      val (sk1, vk1) = Curve25519.createKeyPair
      val (_, vk2) = Curve25519.createKeyPair
      val (sk2, _) = Curve25519.createKeyPair
      val (sk3, vk3) = Curve25519.createKeyPair
      val signatureProposition1 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk1.value))
      val signatureProposition2 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk2.value))
      val signatureProposition3 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk3.value))
      val thresholdProposition = thresholdProposer.propose((Set(signatureProposition1, signatureProposition2, signatureProposition3), 2))
      val signature1 = Curve25519.sign(sk1, signableBytes)
      val signature2 = Curve25519.sign(sk2, signableBytes)
      val signature3 = Curve25519.sign(sk3, signableBytes)
      val signatureProverProof1 = signatureProver.prove(Models.Witness(signature1.value), signableBytes)
      val signatureProverProof2 = signatureProver.prove(Models.Witness(signature2.value), signableBytes)
      val signatureProverProof3 = signatureProver.prove(Models.Witness(signature3.value), signableBytes)
      val thresholdProoverProof = thresholdProver.prove(Set(Some(signatureProverProof1), Some(signatureProverProof2), Some(signatureProverProof3)), signableBytes)
      val result = verifierInstance.evaluate(thresholdProposition, thresholdProoverProof, dynamicContext(thresholdProposition, thresholdProoverProof))
      assertEquals(result.isRight, true)
    }

    test("A threshold proposition must evaluate to false when the threshold is not passed") {
      val (sk1, vk1) = Curve25519.createKeyPair
      val (_, vk2) = Curve25519.createKeyPair
      val (sk2, _) = Curve25519.createKeyPair
      val (sk3, vk3) = Curve25519.createKeyPair
      val (_, vk4) = Curve25519.createKeyPair
      val (_, vk5) = Curve25519.createKeyPair
      val signatureProposition1 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk1.value))
      val signatureProposition2 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk2.value))
      val signatureProposition3 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk3.value))
      val signatureProposition4 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk4.value))
      val signatureProposition5 = signatureProposer.propose("Curve25519", Models.VerificationKey(vk5.value))
      val thresholdProposition = thresholdProposer.propose((Set(signatureProposition1, signatureProposition2, signatureProposition3, signatureProposition4, signatureProposition5), 3))
      val signature1 = Curve25519.sign(sk1, signableBytes)
      val signature2 = Curve25519.sign(sk2, signableBytes)
      val signature3 = Curve25519.sign(sk3, signableBytes)
      val signatureProverProof1 = signatureProver.prove(Models.Witness(signature1.value), signableBytes)
      val signatureProverProof2 = signatureProver.prove(Models.Witness(signature2.value), signableBytes)
      val signatureProverProof3 = signatureProver.prove(Models.Witness(signature3.value), signableBytes)
      val thresholdProoverProof = thresholdProver.prove(Set(Some(signatureProverProof1), Some(signatureProverProof2), Some(signatureProverProof3)), signableBytes)
      val result = verifierInstance.evaluate(thresholdProposition, thresholdProoverProof, dynamicContext(thresholdProposition, thresholdProoverProof))
      assertEquals(result.isLeft, true)
      assertEquals(result.left.toOption.collect({case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) => true}).isDefined, true)
    }

}

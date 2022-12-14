package co.topl.quivr

import cats.Id
import cats.Monad
import quivr.models._
import co.topl.crypto.signatures.Curve25519
import co.topl.quivr.runtime.QuivrRuntimeErrors
import com.google.protobuf.ByteString

/**
 * Set of tests for the Quivr Composite Operations.
 */
class QuivrCompositeOpsTests extends munit.FunSuite with MockHelpers {

  import co.topl.quivr.api.Proposer._

  import co.topl.quivr.api.Prover._

  import co.topl.quivr.api.Verifier.instances._

  implicit val applicativeId: Monad[Id] = cats.catsInstancesForId

  test("An and proposition must evaluate to true when both the verification of both proofs is true") {
    val (sk1, vk1) = Curve25519.createKeyPair
    val (sk2, vk2) = Curve25519.createKeyPair
    val vk1Bytes = vk1.value
    val vk2Bytes = vk2.value
    val signatureProposition1 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk1Bytes)))
    val signatureProposition2 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk2Bytes)))
    val andProposition = andProposer.propose(signatureProposition1, signatureProposition2)
    val signature1 = Curve25519.sign(sk1, signableBytes.value.toByteArray)
    val signature2 = Curve25519.sign(sk2, signableBytes.value.toByteArray)
    val signatureProverProof1 = signatureProver.prove(Witness(ByteString.copyFrom(signature1.value)), signableBytes)
    val signatureProverProof2 = signatureProver.prove(Witness(ByteString.copyFrom(signature2.value)), signableBytes)
    val andProverProof = andProver.prove((signatureProverProof1, signatureProverProof2), signableBytes)
    val result =
      verifierInstance.evaluate(andProposition, andProverProof, dynamicContext(andProposition, andProverProof))
    assertEquals(result.isRight, true)
  }

  test("An and proposition must evaluate to false when one of the proofs evaluates to false") {
    val (sk1, vk1) = Curve25519.createKeyPair
    val (_, vk2) = Curve25519.createKeyPair
    val (sk2, _) = Curve25519.createKeyPair
    val vk1Bytes = vk1.value
    val vk2Bytes = vk2.value
    val signatureProposition1 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk1Bytes)))
    val signatureProposition2 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk2Bytes)))
    val andProposition = andProposer.propose(signatureProposition1, signatureProposition2)
    val signature1 = Curve25519.sign(sk1, signableBytes.value.toByteArray)
    val signature2 = Curve25519.sign(sk2, signableBytes.value.toByteArray)
    val signatureProverProof1 = signatureProver.prove(Witness(ByteString.copyFrom(signature1.value)), signableBytes)
    val signatureProverProof2 = signatureProver.prove(Witness(ByteString.copyFrom(signature2.value)), signableBytes)
    val andProverProof = andProver.prove((signatureProverProof1, signatureProverProof2), signableBytes)
    val result =
      verifierInstance.evaluate(andProposition, andProverProof, dynamicContext(andProposition, andProverProof))
    assertEquals(result.isLeft, true)
    assertEquals(
      result.left.toOption.collect { case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) =>
        true
      }.isDefined,
      true
    )
  }

  test("An or proposition must evaluate to true when one of the proofs evaluates to true") {
    val (sk1, vk1) = Curve25519.createKeyPair
    val (_, vk2) = Curve25519.createKeyPair
    val (sk2, _) = Curve25519.createKeyPair
    val vk1Bytes = vk1.value
    val vk2Bytes = vk2.value
    val signatureProposition1 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk1Bytes)))
    val signatureProposition2 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk2Bytes)))
    val orProposition = orProposer.propose(signatureProposition1, signatureProposition2)
    val signature1 = Curve25519.sign(sk1, signableBytes.value.toByteArray)
    val signature2 = Curve25519.sign(sk2, signableBytes.value.toByteArray)
    val signatureProverProof1 = signatureProver.prove(Witness(ByteString.copyFrom(signature1.value)), signableBytes)
    val signatureProverProof2 = signatureProver.prove(Witness(ByteString.copyFrom(signature2.value)), signableBytes)
    val orProverProof = orProver.prove((signatureProverProof1, signatureProverProof2), signableBytes)
    val result = verifierInstance.evaluate(orProposition, orProverProof, dynamicContext(orProposition, orProverProof))
    assertEquals(result.isRight, true)
  }

  test("An or proposition must evaluate to false when both proofs evaluate to false") {
    val (_, vk1) = Curve25519.createKeyPair
    val (sk1, _) = Curve25519.createKeyPair
    val (_, vk2) = Curve25519.createKeyPair
    val (sk2, _) = Curve25519.createKeyPair
    val vk1Bytes = vk1.value
    val vk2Bytes = vk2.value
    val signatureProposition1 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk1Bytes)))
    val signatureProposition2 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk2Bytes)))
    val orProposition = orProposer.propose(signatureProposition1, signatureProposition2)
    val signature1 = Curve25519.sign(sk1, signableBytes.value.toByteArray)
    val signature2 = Curve25519.sign(sk2, signableBytes.value.toByteArray)
    val signatureProverProof1 = signatureProver.prove(Witness(ByteString.copyFrom(signature1.value)), signableBytes)
    val signatureProverProof2 = signatureProver.prove(Witness(ByteString.copyFrom(signature2.value)), signableBytes)
    val orProverProof = orProver.prove((signatureProverProof1, signatureProverProof2), signableBytes)
    val result = verifierInstance.evaluate(orProposition, orProverProof, dynamicContext(orProposition, orProverProof))
    assertEquals(result.isLeft, true)
    assertEquals(
      result.left.toOption.collect { case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) =>
        true
      }.isDefined,
      true
    )
  }

  test("A not proposition must evaluate to false when the proof in the parameter is true") {
    val heightProposition = heightProposer.propose("height", 900, 1000)
    val heightProverProof = heightProver.prove((), signableBytes)
    val notProposition = notProposer.propose(heightProposition)
    val notProverProof = notProver.prove(heightProverProof, signableBytes)
    val result =
      verifierInstance.evaluate(notProposition, notProverProof, dynamicContext(notProposition, notProverProof))
    assertEquals(result.isLeft, true)
    assertEquals(
      result.left.toOption.collect { case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) =>
        true
      }.isDefined,
      true
    )
  }

  test("A not proposition must evaluate to true when the proof in the parameter is false") {
    val heightProposition = heightProposer.propose("height", 1, 10)
    val heightProverProof = heightProver.prove((), signableBytes)
    val notProposition = notProposer.propose(heightProposition)
    val notProverProof = notProver.prove(heightProverProof, signableBytes)
    val result =
      verifierInstance.evaluate(notProposition, notProverProof, dynamicContext(notProposition, notProverProof))
    assertEquals(result.isRight, true)
  }

  test("A threshold proposition must evaluate to true when the threshold is passed") {
    val (sk1, vk1) = Curve25519.createKeyPair
    val (_, vk2) = Curve25519.createKeyPair
    val (sk2, _) = Curve25519.createKeyPair
    val (sk3, vk3) = Curve25519.createKeyPair
    val vk1Bytes = vk1.value
    val vk2Bytes = vk2.value
    val vk3Bytes = vk3.value
    val signatureProposition1 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk1Bytes)))
    val signatureProposition2 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk2Bytes)))
    val signatureProposition3 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk3Bytes)))
    val thresholdProposition =
      thresholdProposer.propose((Set(signatureProposition1, signatureProposition2, signatureProposition3), 2))
    val signature1 = Curve25519.sign(sk1, signableBytes.value.toByteArray)
    val signature2 = Curve25519.sign(sk2, signableBytes.value.toByteArray)
    val signature3 = Curve25519.sign(sk3, signableBytes.value.toByteArray)
    val signatureProverProof1 = signatureProver.prove(Witness(ByteString.copyFrom(signature1.value)), signableBytes)
    val signatureProverProof2 = signatureProver.prove(Witness(ByteString.copyFrom(signature2.value)), signableBytes)
    val signatureProverProof3 = signatureProver.prove(Witness(ByteString.copyFrom(signature3.value)), signableBytes)
    val thresholdProverProof = thresholdProver.prove(
      Set(Some(signatureProverProof1), Some(signatureProverProof2), Some(signatureProverProof3)),
      signableBytes
    )
    val result = verifierInstance.evaluate(
      thresholdProposition,
      thresholdProverProof,
      dynamicContext(thresholdProposition, thresholdProverProof)
    )
    assertEquals(result.isRight, true)
  }

  test("A threshold proposition must evaluate to false when the threshold is not passed") {
    val (sk1, vk1) = Curve25519.createKeyPair
    val (_, vk2) = Curve25519.createKeyPair
    val (sk2, _) = Curve25519.createKeyPair
    val (sk3, vk3) = Curve25519.createKeyPair
    val (_, vk4) = Curve25519.createKeyPair
    val (_, vk5) = Curve25519.createKeyPair
    val vk1Bytes = vk1.value
    val vk2Bytes = vk2.value
    val vk3Bytes = vk3.value
    val vk4Bytes = vk4.value
    val vk5Bytes = vk5.value
    val signatureProposition1 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk1Bytes)))
    val signatureProposition2 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk2Bytes)))
    val signatureProposition3 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk3Bytes)))
    val signatureProposition4 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk4Bytes)))
    val signatureProposition5 = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vk5Bytes)))
    val thresholdProposition = thresholdProposer.propose(
      (
        Set(
          signatureProposition1,
          signatureProposition2,
          signatureProposition3,
          signatureProposition4,
          signatureProposition5
        ),
        3
      )
    )
    val signature1 = Curve25519.sign(sk1, signableBytes.value.toByteArray)
    val signature2 = Curve25519.sign(sk2, signableBytes.value.toByteArray)
    val signature3 = Curve25519.sign(sk3, signableBytes.value.toByteArray)
    val signatureProverProof1 = signatureProver.prove(Witness(ByteString.copyFrom(signature1.value)), signableBytes)
    val signatureProverProof2 = signatureProver.prove(Witness(ByteString.copyFrom(signature2.value)), signableBytes)
    val signatureProverProof3 = signatureProver.prove(Witness(ByteString.copyFrom(signature3.value)), signableBytes)
    val thresholdProverProof = thresholdProver.prove(
      Set(Some(signatureProverProof1), Some(signatureProverProof2), Some(signatureProverProof3)),
      signableBytes
    )
    val result = verifierInstance.evaluate(
      thresholdProposition,
      thresholdProverProof,
      dynamicContext(thresholdProposition, thresholdProverProof)
    )
    assertEquals(result.isLeft, true)
    assertEquals(
      result.left.toOption.collect { case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) =>
        true
      }.isDefined,
      true
    )
  }

}

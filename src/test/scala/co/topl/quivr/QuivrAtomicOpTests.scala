package co.topl.quivr

import cats.Id
import cats.Monad
import co.topl.brambl.routines.digests.Blake2b256Digest
import co.topl.crypto.signatures.Curve25519
import co.topl.quivr.runtime.QuivrRuntimeErrors
import com.google.protobuf.ByteString
import quivr.models._

/**
 * Set of tests for the Quivr Atomic Operations.
 */
class QuivrAtomicOpTests extends munit.FunSuite with MockHelpers {

  import co.topl.quivr.api.Proposer._

  import co.topl.quivr.api.Prover._

  import co.topl.quivr.api.Verifier.instances._

  implicit val applicativeId: Monad[Id] = cats.catsInstancesForId

  test("A locked proposition must return an LockedPropositionIsUnsatisfiable when evaluated") {
    val lockedProposition = LockedProposer.propose(None)
    val lockedProverProof = lockedProver.prove((), signableBytes)
    val result = verifierInstance.evaluate(
      lockedProposition,
      lockedProverProof,
      dynamicContext(lockedProposition, lockedProverProof)
    )
    assertEquals(result.isLeft, true)
    assertEquals(
      result.left.toOption.collect { case QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable =>
        true
      }.isDefined,
      true
    )
  }

  test("A tick proposition must evaluate to true when tick is in range") {
    val tickProposition = tickProposer.propose(900, 1000)
    val tickProverProof = tickProver.prove((), signableBytes)
    val result =
      verifierInstance.evaluate(tickProposition, tickProverProof, dynamicContext(tickProposition, tickProverProof))
    assertEquals(result.isRight, true)
  }

  test("A tick position must evaluate to false when the tick is not in range") {
    val tickProposition = tickProposer.propose(1, 10)
    val tickProverProof = tickProver.prove((), signableBytes)
    val result =
      verifierInstance.evaluate(tickProposition, tickProverProof, dynamicContext(tickProposition, tickProverProof))
    assertEquals(result.isLeft, true)
    assertEquals(
      result.left.toOption.collect { case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) =>
        true
      }.isDefined,
      true
    )
  }

  test("A height proposition must evaluate to true when height is in range") {
    val heightProposition = heightProposer.propose("height", 900, 1000)
    val heightProverProof = heightProver.prove((), signableBytes)
    val result = verifierInstance.evaluate(
      heightProposition,
      heightProverProof,
      dynamicContext(heightProposition, heightProverProof)
    )
    assertEquals(result.isRight, true)
  }

  test("A height proposition must evaluate to false when height is not in range") {
    val heightProposition = heightProposer.propose("height", 1, 10)
    val heightProverProof = heightProver.prove((), signableBytes)
    val result = verifierInstance.evaluate(
      heightProposition,
      heightProverProof,
      dynamicContext(heightProposition, heightProverProof)
    )
    assertEquals(result.isLeft, true)
    assertEquals(
      result.left.toOption.collect { case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) =>
        true
      }.isDefined,
      true
    )
  }

  test("A signature proposition must evaluate to true when the signature proof is correct") {
    val (sk, vk) = Curve25519.createKeyPair
    val vkBytes = vk.value
    val signatureProposition = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vkBytes)))
    val signature = Curve25519.sign(sk, signableBytes.value.toByteArray)
    val signatureProverProof = signatureProver.prove(Witness(ByteString.copyFrom(signature.value)), signableBytes)
    val result = verifierInstance.evaluate(
      signatureProposition,
      signatureProverProof,
      dynamicContext(signatureProposition, signatureProverProof)
    )
    assertEquals(result.isRight, true)
  }

  test("A signature proposition must evaluate to false when the signature proof is not correct") {
    val (_, vk) = Curve25519.createKeyPair
    val vkBytes = vk.value
    val (sk, _) = Curve25519.createKeyPair
    val signatureProposition = signatureProposer.propose("Curve25519", VerificationKey(ByteString.copyFrom(vkBytes)))
    val signature = Curve25519.sign(sk, signableBytes.value.toByteArray)
    val signatureProverProof = signatureProver.prove(Witness(ByteString.copyFrom(signature.value)), signableBytes)
    val result = verifierInstance.evaluate(
      signatureProposition,
      signatureProverProof,
      dynamicContext(signatureProposition, signatureProverProof)
    )
    assertEquals(result.isLeft, true)
    assertEquals(
      result.left.toOption.collect { case QuivrRuntimeErrors.ValidationError.EvaluationAuthorizationFailed(_, _) =>
        true
      }.isDefined,
      true
    )
  }

  test("A digest proposition must evaluate to true when the digest is correct") {
    val mySalt = ByteString.copyFromUtf8("I am a digest")
    val myPreimage = Preimage(ByteString.copyFromUtf8("I am a preimage"), mySalt)
    val myDigest = Blake2b256Digest.hash(myPreimage)
    val digestProposition = digestProposer.propose(("blake2b256", myDigest))
    val digestProverProof = digestProver.prove(myPreimage, signableBytes)
    val result = verifierInstance.evaluate(
      digestProposition,
      digestProverProof,
      dynamicContext(digestProposition, digestProverProof)
    )
    assertEquals(result.isRight, true)
  }

  test("A digest proposition must evaluate to false when the digest is incorrect") {
    val mySalt = ByteString.copyFromUtf8("I am a digest")
    val myPreimage = Preimage(ByteString.copyFromUtf8("I am a preimage"), mySalt)
    val myDigest = Blake2b256Digest.hash(myPreimage)
    val wrongPreImage = Preimage(ByteString.copyFromUtf8("I am a wrong preimage"), mySalt)
    val digestProposition = digestProposer.propose(("blake2b256", myDigest))
    val digestProverProof = digestProver.prove(wrongPreImage, signableBytes)
    val result = verifierInstance.evaluate(
      digestProposition,
      digestProverProof,
      dynamicContext(digestProposition, digestProverProof)
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

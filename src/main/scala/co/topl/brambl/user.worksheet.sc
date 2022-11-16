// Examples of how *Brambl* Quivr and Credentials will be called by a user of the SDK

import co.topl.quivr.Models.Primitive._
import co.topl.quivr.SignableTxBytes
import co.topl.quivr.{Proof, Proposer, Proposition, Prover, User, Verifier}
import co.topl.crypto.hash.blake2b256
import co.topl.quivr.runtime.DynamicContext
import co.topl.node.Tetra.Predicate

type Trivial[T] = T
// An Opinionated Verification Context
val ctx: DynamicContext[Trivial, String] = ???


// ==== The following simply uses the building blocks that were already created

// Create Digest Proposition
val preImage: Array[Byte] = "random_data".getBytes
val digest: Array[Byte] = blake2b256.hash(preImage).value
val preImageObj: User.Preimage = User.Digests.Preimage(preImage)
val digestObj: User.Digest = User.Digests.Digest(preImage.length.toByte, digest) // not sure if first param is correct
val proposition: Proposition = Proposer.digestProposer[Trivial, (String, User.Digest)].propose(("temp", digestObj))

// Create Digest Proof
val message: SignableTxBytes = ???
// Not sure if I instantiated the proof in the best way/as intended
val prover: Prover[Trivial, (Byte, User.Preimage)] = Prover.instances.proverInstance
val proof: Proof = prover.prove((Digest.token, preImageObj), message)

// Verify. Verifier does not need to know what kind of Proposition or Proof it's verifying.
val verifier: Verifier[Trivial] = Verifier.instances.verifierInstance
val isVerified: Boolean = verifier.evaluate(proposition, proof, ctx)



// ==== The following is an example of how a Predicate can be create (if have access to all the data)
val predicate: Predicate = Predicate(List(proposition), 1) // Simple 1 of 1 digest predicate



// ==== The following is the beginning of thinking through how an Attestation can be created from a predicate
// TODO: Need to work through 1) how to generate a predicate image 2) Predicate image + Predicate.Known + proofs => attestation

val digest1: Array[TypedEvidence] = predicate.conditions.map(propositionDigest).toArray
val challenges1: List[Proposition] = predicate.conditions
val responses1: List[Proof] = predicate.conditions.map(???) // Challenge: How to get proofs without explicitly knowing the propositin type

// Alternative: Construct manually. Although will this be possible for unprovenTx => provenTx? Need to think through
val digest2: Array[TypedEvidence] = Array(propositionDigest(proposition))

val attestation: Attestation = Attestation(Predicate.Image(digest2, 1), List(Option(proposition, proof)))


// Alternative 2: Construct via Predicate *Image*. This is necessary if we want functionality for "proving an unproven transaction"
// See playground.worksheet.sc
// Examples of how *Brambl* Quivr and Credentials will be called by a user of the SDK

import co.topl.quivr.Models.Primitive._
import co.topl.quivr.SignableTxBytes
import co.topl.quivr.{Proof, Proposer, Proposition, Prover, Verifier}
import co.topl.crypto.hash.blake2b256
import co.topl.quivr.runtime.DynamicContext
import co.topl.brambl.{Credentials, TransactionBuilder}
import co.topl.brambl.Models._
import co.topl.node.Tetra.Box
import co.topl.common.Digests


// ==== The following simply uses the building blocks that were already created
def quivrExample: Unit = {
  type Trivial[T] = T
  // An Opinionated Verification Context
  val ctx: DynamicContext[Trivial, String] = ???

  // Create Digest Proposition
  val preImage: Array[Byte] = "random_data".getBytes
  val digest: Array[Byte] = blake2b256.hash(preImage).value
  val preImageObj: Digests.Preimage = Digests.Preimage(preImage, 0)
  val digestObj: Digests.Digest = Digests.Digest(digest) // not sure if first param is correct
  val proposition: Proposition = Proposer.digestProposer[Trivial, (String, Digests.Digest)].propose(("temp", digestObj))

  // Create Digest Proof
  val message: SignableTxBytes = ???
  // Not sure if I instantiated the proof in the best way/as intended
  val prover: Prover[Trivial, (Byte, Digests.Preimage)] = Prover.instances.proverInstance
  val proof: Proof = prover.prove((Digest.token, preImageObj), message)

  // Verify. Verifier does not need to know what kind of Proposition or Proof it's verifying.
  val verifier: Verifier[Trivial] = Verifier.instances.verifierInstance
  val isVerified: Boolean = verifier.evaluate(proposition, proof, ctx)
}


// The following are examples for Transaction builder and Credentials

// Arbitrary data
val idx1 = Indices(0, 0, 1)
val idx2 = Indices(0, 0, 2)
val idx3 = Indices(0, 0, 3)
val value = Box.Values.Token(1)

// Generate and prove t1
val unprovenT1 = TransactionBuilder.buildUnprovenTransaction(idx1, idx2, value)
val t1 = Credentials.proveTransaction(unprovenT1)

// Generate and prove t2
val unprovenT2 = TransactionBuilder.buildUnprovenTransaction(idx2,idx3,value)
val t2 = Credentials.proveTransaction(unprovenT2)
// Examples of how *Brambl* Quivr and Credentials will be called by a user of the SDK

import co.topl.quivr.Models.Primitive._
import co.topl.quivr.SignableTxBytes
import co.topl.quivr.{Proof, Proposer, Proposition, Prover, User, Verifier}
import co.topl.crypto.hash.blake2b256
import co.topl.quivr.runtime.DynamicContext
import co.topl.brambl.TransactionBuilder
import co.topl.brambl.Indices
import co.topl.node.Tetra.{Datums, Box, IoTx}
import co.topl.node.Models.Metadata

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


val i = 0 // Arbitrary x to refer to "Alice"
val j = 0 // Arbitrary y to refer to 1 of 1 predicate
val k1 = 0 // Arbitrary z to refer to the utxo in the first transaction
val k2 = 1 // Arbitrary z to refer to the utxo in the second transaction
val k3 = 2 // Arbitrary z to refer to the utxo in the third transaction

val idx1 = Indices(i, j, k1)
val idx2 = Indices(i, j, k2)
val idx3 = Indices(i, j, k3)

// arbitrary box value
val value = Box.Values.Token(1)
// arbitrary schedule
val schedule = IoTx.Schedule(0, 0, 0)
// Arbitrary meta
val meta = None

val unprovenT1 = TransactionBuilder.buildUnprovenTransaction(
  idx1,
  Datums.SpentOutput(meta),
  idx2,
  Datums.UnspentOutput(meta),
  value,
  schedule,
  meta
)

val t1 = TransactionBuilder.proveTransaction(unprovenT1)

val unprovenT2 = TransactionBuilder.buildUnprovenTransaction(
  idx2,
  Datums.SpentOutput(meta),
  idx3,
  Datums.UnspentOutput(meta),
  value,
  schedule,
  meta
)

val t2 = TransactionBuilder.proveTransaction(unprovenT2)
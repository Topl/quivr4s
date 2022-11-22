import co.topl.quivr.Models.Primitive
import co.topl.quivr.SignableTxBytes
import co.topl.quivr.{Proof, Proposer, Proposition, Prover, Verifier}
import co.topl.crypto.hash.blake2b256
import co.topl.quivr.runtime.DynamicContext
import co.topl.brambl.{Credentials, TransactionBuilder}
import co.topl.brambl.Models._
import co.topl.node.Tetra.Box
import co.topl.common.{Digest, Preimage}
import co.topl.genus.Models.Txo
import co.topl.brambl.QuivrService
import co.topl.brambl.Storage.getTxo

// Examples of how *Brambl* Quivr and Credentials will be called by a user of the SDK

/***
 * Examples directly using the quivr library API; Proposer, Prover, and Verifier
 */
def quivrExample: Unit = {
  type Trivial[T] = T
  // An Opinionated Verification Context
  val ctx: DynamicContext[Trivial, String] = ???

  // Proposer: Create Digest Proposition
  val preImage: Array[Byte] = "random_data".getBytes
  val digest: Array[Byte] = blake2b256.hash(preImage).value
  val preImageObj: Preimage = Preimage(preImage, Array(0: Byte))
  val digestObj: Digest = Digest(digest)
  val proposition: Proposition = Proposer.digestProposer[Trivial, (String, Digest)].propose(("temp", digestObj))

  // Prover: Create Digest Proof
  val message: SignableTxBytes = ???
  // Not sure if I instantiated the proof as it was intended
  val prover: Prover[Trivial, (Byte, Preimage)] = Prover.instances.proverInstance
  val proof: Proof = prover.prove((Primitive.Digest.token, preImageObj), message)

  // Verifier: Verify if a Proof satisfies a Proposition given a Context
  // Verifier does not need to know what kind of Proposition or Proof it's verifying.
  val verifier: Verifier[Trivial] = Verifier.instances.verifierInstance
  val isVerified: Boolean = verifier.evaluate(proposition, proof, ctx)
}


// The following are examples for Transaction builder and Credentials.
val value = Box.Values.Token(1) // Arbitrary data

// indices version

// Arbitrary data
val idx1 = Indices(0, 0, 1)
val idx2 = Indices(0, 0, 2)
val idx3 = Indices(0, 0, 3)

// Generate and prove t1
val unprovenT1V1 = TransactionBuilder.buildUnprovenIoTxV1(idx1, idx2, value)
val t1V1 = Credentials.proveIoTx[UnprovenSpentOutputV1](unprovenT1V1)
val isT1V1Verified = QuivrService.verifyIoTx(t1V1)

// Generate and prove t2
val unprovenT2V1 = TransactionBuilder.buildUnprovenIoTxV1(idx2,idx3,value)
val t2V1 = Credentials.proveIoTx[UnprovenSpentOutputV1](unprovenT2V1)
val isT2V1Verified = QuivrService.verifyIoTx(t2V1)


// txo version

// Arbitrary data
// A TXO should be retrieved from Genus, but for now will search by indices
val txo1: Txo = getTxo(idx1)

val unprovenT1V2 = TransactionBuilder.buildUnprovenIoTxV2(txo1, value)
val t1V2 = Credentials.proveIoTx[UnprovenSpentOutputV2](unprovenT1V2)
val isT1V2Verified = QuivrService.verifyIoTx(t1V2)


// The t1V2 should be broadcast to the chain. After which Genus can inform us of a new Txo

// A TXO should be retrieved from Genus, but for now will search by indices
val txo2: Txo = getTxo(idx2)

val unprovenT2V2 = TransactionBuilder.buildUnprovenIoTxV2(txo2, value)
val t2V2 = Credentials.proveIoTx[UnprovenSpentOutputV2](unprovenT2V2)
val isT2V2Verified = QuivrService.verifyIoTx(t2V2)

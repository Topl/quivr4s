// Examples of how *Brambl* Quivr and Credentials will be called by a user of the SDK

import co.topl.brambl.Credentials
import co.topl.brambl.Tx
import co.topl.brambl.Tetra.{Attestation, Box, Datums, Predicate}
import co.topl.brambl.Tetra.Predicate.TypedEvidence
import co.topl.quivr.Evaluation
import co.topl.quivr.SignableTxBytes
import co.topl.quivr.{Proof, Proposer, Proposition, Prover, Verifier}

type Trivial[T] = T
// An Opinionated Verification Context
case class ToplContext(tx: Tx) extends Evaluation.DynamicContext[Trivial, String] {
  override val datums = Map(
    // Dummy Data
    "eon" -> Datums.Eon(10, 2), // <- not sure what "beginSlot" is referring to. First slot of the eon?
    "era" -> Datums.Era(22, 4),
    "epoch" -> Datums.Epoch(34, 6),
    "block" -> Datums.Header(24) // chain is 24 blocks long
  )
  override val interfaces = ???
  override val signingRoutines = ???
  override val hashingRoutines = ???

  override def signableBytes: SignableTxBytes = tx.getSignableBytes
  override def currentTick: Long = 250L // Arbitrary value
}

// Temporarily abstracting away how the typed evidence digest will be generated from the Propositions themselves
def propositionDigest(prop: Proposition): TypedEvidence = ???


// ==== The following simply uses the building blocks that were already created

// Create Height Lock Proposition: Requires that the block headers are height 8-12 inclusive
val proposition: Proposition = Proposer.heightProposer[Trivial, (String, Long, Long)].propose("block", 8, 12)
// Create Height Lock Proof
// "utxo" is a placeholder for the ID of an already existing utxo. Ideally the data should come from Credentials
val ioTx = Credentials.getIoTxByBox(Box.Id("utxo".getBytes()))
val proof: Proof = Prover.heightProver[Trivial].prove({}, ioTx.getSignableBytes)
// Verify. Verifier does not need to know what kind of Proposition or Proof it's verifying.
val isVerified = Verifier.Instances.verifierInstance[Trivial].evaluate(proposition, proof, ToplContext(ioTx))




// ==== The following is an example of how a Predicate can be create (if have access to all the data)
val predicate: Predicate = Predicate(List(proposition), 1) // Simple 1 of 1 height lock predicate



// ==== The following is the beginning of thinking through how an Attestation can be created from a predicate
val digest1: Array[TypedEvidence] = predicate.conditions.map(propositionDigest(_)).toArray
val challenges1: List[Proposition] = predicate.conditions
val responses1: List[Proof] = predicate.conditions.map(???) // Challenge: How to get proofs without explicitly knowing the propositin type

// Alternative: Construct manually. Although will this be possible for unprovenTx => provenTx? Need to think through
val digest2: Array[TypedEvidence] = Array(propositionDigest(proposition))

val attestation: Attestation = Attestation(Predicate.Image(digest2, 1), List(Option(proposition, proof)))

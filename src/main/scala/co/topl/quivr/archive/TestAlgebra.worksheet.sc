import co.topl.quivr.archive.Box
import co.topl.quivr.archive.algebras.QuivrContractAlgebraSyntax._
import co.topl.quivr.archive.algebras._
import co.topl.quivr.archive.interpreters.NativeTransaction
import scorex.crypto.signatures.Curve25519

val keyPair1 = Curve25519.createKeyPair("test0".getBytes())

val keyPair2 = Curve25519.createKeyPair("test1".getBytes())

val keyPair3 = Curve25519.createKeyPair("test2".getBytes())

val listVks = List(keyPair1._2, keyPair2._2, keyPair3._2)

val listSks = List(keyPair1._1, keyPair2._1, keyPair3._1)

val otherListSks = List(keyPair1._1, keyPair3._1, keyPair2._1)

val contract = heightLock(5)

// val contractold = and(heightLock(5), and(signature(1), signature(2)))

val otherContract = and(signature(1), signature(3))

val propositionCtx = ProposerContext(listVks)

val proposition = Proposer.createProposition(contract)(propositionCtx)

val transaction = NativeTransaction[Box.Value](
  List(
    // STxO(
    //   Box.Id("1".getBytes()),
    //   Contract(Set(proposition), 1),
    //   Attestation(Set(proof)),
    //   Box.Values.Lvl(1, Box.Datum(None)),
    //   None
    // )
  ),
  List(
    // UTxO(
    //   Box.Id("2".getBytes()),
    //   Box.Values.Lvl(1, Box.Datum(None)),
    //   None
    // )
  ),
  None
)

val propositionOtherContract =
  Proposer.createProposition(otherContract)(propositionCtx)
val proverCtx = ProverContext(
  listVks,
  listSks,
  transaction
)

val proof = Prover.prove(contract)(proverCtx)

implicit val verifierCtx =
  VerifierContext(listVks, 5L, transaction)

Verifier.eval(Verifier.verify(proposition, proof)(verifierCtx))

Verifier.eval(Verifier.verify(proposition, proof)(verifierCtx))

// println(res)

package co.topl.brambl

import co.topl.common.Data
import co.topl.common.Models.{Digest, DigestVerification, Preimage}
import co.topl.quivr.Models.Primitive
import co.topl.quivr.runtime.DynamicContext
import co.topl.node.transaction.authorization.ValidationInterpreter
import co.topl.quivr.api.{Proposer, Prover, Verifier}
import co.topl.node.transaction.{Datums, IoTransaction}
import co.topl.quivr.{SignableBytes}
import co.topl.crypto.hash.blake2b256
import co.topl.node.Events
import co.topl.node.typeclasses.ContainsSignable.instances.ioTransactionSignable
import co.topl.quivr.algebras.DigestVerifier
import co.topl.quivr.runtime.{Datum, QuivrRuntimeError, QuivrRuntimeErrors}

// Easy to use Topl-opinionated layer for Brambl to use to access the un-opinionated quivr API

object QuivrService {

  case class DigestValidator() extends DigestVerifier[Option] {
    override def validate(v: DigestVerification): Option[Either[QuivrRuntimeError, DigestVerification]] = {
      val test = blake2b256.hash(v.preimage.input ++ v.preimage.salt).value
      val expected = v.digest.value

      Option(
        if (expected sameElements test) Right(v)
        else Left(QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable)
      )
    }
  }

  // An Opinionated Verification Context
  case class ToplContext(tx: IoTransaction) extends DynamicContext[Option, String] {
    override val datums: Map[String, Datum[_]] = Map(
      // Datums with height => Not needed for digest operations
      "eon" -> Datums.eonDatum(Events.Eon(10, 2)), // <- not sure what "beginSlot" is referring to. First slot of the eon?
      "era" -> Datums.eraDatum(Events.Era(22, 4)),
      "epoch" -> Datums.epochDatum(Events.Epoch(34, 6)),
      "header" -> Datums.headerDatum(Events.Header(24)),
      "root" -> Datums.rootDatum(Events.Root(Array(0: Byte)))
    )
    override val hashingRoutines = Map("blake2b256" -> DigestValidator())
    override def signableBytes: Option[SignableBytes] = Option(ioTransactionSignable.signableBytes(tx))

    // Arbitrary values
    override val interfaces = Map()
    override val signingRoutines = Map()
    override def currentTick: Option[Long] = Option(250L)

  }

  def lockedProposition: Primitive.Locked.Proposition =
    Proposer.LockedProposer[Option, Option[Data]].propose(None).get

  def lockedProof(msg: SignableBytes): Primitive.Locked.Proof =
    Prover.lockedProver[Option].prove((), msg).get

  def digestProposition(digest: Digest): Primitive.Digest.Proposition =
    Proposer.digestProposer[Option, (String, Digest)].propose(("blake2b256", digest)).get
  def digestProof(msg: SignableBytes, preimage: Preimage): Primitive.Digest.Proof =
    Prover.digestProver[Option].prove(preimage, msg).get

  def validate(tx: IoTransaction): Boolean = {
    val context: ToplContext = ToplContext(tx)
    implicit val verifier: Verifier[Option] = Verifier.instances.verifierInstance
    ValidationInterpreter
      .make[Option]()
      .validate(context)(tx)
      .get
      .isRight
  }
}

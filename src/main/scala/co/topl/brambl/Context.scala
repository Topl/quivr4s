package co.topl.brambl

import co.topl.node.Events
import co.topl.node.transaction.{Datums, IoTransaction}
import co.topl.node.typeclasses.ContainsSignable.instances.ioTransactionSignable
import co.topl.quivr.SignableBytes
import co.topl.quivr.runtime.{Datum, DynamicContext}
import co.topl.common.Models.{DigestVerification, SignatureVerification}
import co.topl.quivr.algebras.{DigestVerifier, SignatureVerifier}
import co.topl.quivr.runtime.{QuivrRuntimeError, QuivrRuntimeErrors}
import co.topl.crypto.hash.blake2b256
import co.topl.crypto.signatures.{Curve25519, Signature}
import co.topl.crypto.PublicKey

object Context {
  private case class DigestValidator() extends DigestVerifier[Option] {
    override def validate(v: DigestVerification): Option[Either[QuivrRuntimeError, DigestVerification]] = {
      val test = blake2b256.hash(v.preimage.input ++ v.preimage.salt).value
      val expected = v.digest.value

      Option(
        if (expected sameElements test) Right(v)
        else Left(QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable)
      )
    }
  }

  private case class SignatureValidator() extends SignatureVerifier[Option] {
    override def validate(v: SignatureVerification): Option[Either[QuivrRuntimeError, SignatureVerification]] = {
      val test = Curve25519.verify(Signature(v.sig.value), v.msg.value, PublicKey(v.vk.value))

      Option(
        if (test) Right(v)
        else Left(QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable)
      )
    }
  }


  // An Opinionated Verification Context
  private case class ToplContext(tx: IoTransaction, curTick: Option[Long], heightDatums: String => Option[Datum[_]])
    extends DynamicContext[Option, String] {
    override val hashingRoutines: Map[String, DigestValidator] = Map("blake2b256" -> DigestValidator())
    override val signingRoutines: Map[String, SignatureValidator] = Map("curve25519" -> SignatureValidator())
    override val interfaces = Map() // Arbitrary


    override def signableBytes: Option[SignableBytes] = Option(ioTransactionSignable.signableBytes(tx))


    // The following 2
    override def currentTick: Option[Long] = curTick

    private val heightData: Map[String, Datum[_]] = Map(
      "eon" -> Datums.eonDatum(Events.Eon(10, 2)), // <- not sure what "beginSlot" is referring to. First slot of the eon?
      "era" -> Datums.eraDatum(Events.Era(22, 4)),
      "epoch" -> Datums.epochDatum(Events.Epoch(34, 6)),
      "header" -> Datums.headerDatum(Events.Header(24)),
      "root" -> Datums.rootDatum(Events.Root(Array(0: Byte)))
    )
    // Needed for height
    override val datums: String => Option[Datum[_]] = heightDatums
  }

  def getContext(tx: IoTransaction)(implicit curTick: Option[Long],
                                    heightDatums: String => Option[Datum[_]]): DynamicContext[Option, String] =
    ToplContext(tx, curTick, heightDatums)
}

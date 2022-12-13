package co.topl.brambl

import co.topl.node.transaction.IoTransaction
import co.topl.node.typeclasses.ContainsSignable.instances.ioTransactionSignable
import co.topl.quivr.SignableBytes
import co.topl.quivr.runtime.{Datum, DynamicContext}
import co.topl.common.Models.{DigestVerification, SignatureVerification}
import co.topl.quivr.algebras.{DigestVerifier, SignatureVerifier}
import co.topl.quivr.runtime.{QuivrRuntimeError, QuivrRuntimeErrors}
import co.topl.crypto.hash.blake2b256
import co.topl.crypto.signatures.{Curve25519, Signature}
import co.topl.crypto.{PublicKey, signatures}

object Context {
  // Blake2b256 Digest Validator
  private case class Blake2b256Validator() extends DigestVerifier[Option] {
    override def validate(v: DigestVerification): Option[Either[QuivrRuntimeError, DigestVerification]] = {
      val test = blake2b256.hash(v.preimage.input ++ v.preimage.salt).value
      Some(
        if(
          v.digest.value.sameElements(test)
        ) Right(v)
        else Left(QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable)
      )
    }
  }

  // Curve25519 Signature validator
  private case class Curve25519Validator() extends SignatureVerifier[Option] {
    override def validate(v: SignatureVerification): Option[Either[QuivrRuntimeError, SignatureVerification]] =
      Some(
        if(
          signatures.Curve25519.verify(signatures.Signature(v.sig.value), v.msg.value, PublicKey(v.vk.value))
        ) Right(v)
        else Left(QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable)
      )
  }

  // A Verification Context opinionated to the Topl context.
  // signableBytes, currentTick and the datums are dynamic
  case class ToplContext(tx: IoTransaction, curTick: Long, heightDatums: String => Option[Datum[_]])
    extends DynamicContext[Option, String] {
    override val hashingRoutines: Map[String, DigestVerifier[Option]] = Map("blake2b256" -> Blake2b256Validator())
    override val signingRoutines: Map[String, SignatureVerifier[Option]] = Map("curve25519" -> Curve25519Validator())
    override val interfaces = Map() // Arbitrary


    override def signableBytes: Option[SignableBytes] = Option(ioTransactionSignable.signableBytes(tx))
    override def currentTick: Option[Long] = Some(curTick)
    // Needed for height
    override val datums: String => Option[Datum[_]] = heightDatums
  }
}

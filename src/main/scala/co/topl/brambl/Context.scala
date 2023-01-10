package co.topl.brambl

import co.topl.brambl.digests.{Blake2b256Digest, Hash}
import co.topl.brambl.models.Datum
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.signatures.{Curve25519Signature, Signing}
import co.topl.brambl.typeclasses.ContainsSignable.instances.ioTransactionSignable
import co.topl.quivr.runtime.DynamicContext
import co.topl.quivr.algebras.{DigestVerifier, SignatureVerifier}
import quivr.models.SignableBytes

// A Verification Context opinionated to the Topl context.
// signableBytes, currentTick and the datums are dynamic
case class Context(tx: IoTransaction, curTick: Long, heightDatums: String => Option[Datum])
    extends DynamicContext[Option, String, Datum] {

  override val hashingRoutines: Map[String, DigestVerifier[Option] with Hash] =
    Map("blake2b256" -> Blake2b256Digest)

  override val signingRoutines: Map[String, SignatureVerifier[Option] with Signing] =
    Map("curve25519" -> Curve25519Signature)
  override val interfaces = Map() // Arbitrary

  override def signableBytes: Option[SignableBytes] = Option(ioTransactionSignable.signableBytes(tx))

  override def currentTick: Option[Long] = Some(curTick)

  // Needed for height
  override val datums: String => Option[Datum] = heightDatums

  def heightOf(label: String): Option[Option[Long]] = heightDatums(label).map(_.value match {
    case Datum.Value.Header(h) => h.event.map(_.height)
    case _                     => None
  })
}

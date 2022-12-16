package co.topl.quivr

import cats.Id
import co.topl.common.Models
import co.topl.common.ParsableDataInterface
import co.topl.crypto.PublicKey
import co.topl.crypto.signatures.Curve25519
import co.topl.crypto.signatures.Signature
import co.topl.quivr.SignableBytes
import co.topl.quivr.algebras.DigestVerifier
import co.topl.quivr.algebras.SignatureVerifier
import co.topl.quivr.runtime.Datum
import co.topl.quivr.runtime.DynamicContext
import co.topl.quivr.runtime.IncludesHeight
import co.topl.quivr.runtime.QuivrRuntimeError
import co.topl.quivr.runtime.QuivrRuntimeErrors

trait MockHelpers {

  val signableBytes = "someSignableBytes".getBytes()

  def dynamicContext(proposition: Proposition, proof: Proof) = new DynamicContext[Id, String] {

    val mapOfDatums: Map[String, Datum[_]] = Map("height" -> new IncludesHeight[Int] {
      override val height: Long = 999
      override val event: Int = 0
    })

    val mapOfInterfaces: Map[String, ParsableDataInterface] = Map()

    val mapOfSigningRoutines: Map[String, SignatureVerifier[Id]] = Map("Curve25519" -> new SignatureVerifier[Id] {

      override def validate(
        t: Models.SignatureVerification
      ): Id[Either[QuivrRuntimeError, Models.SignatureVerification]] =
        Curve25519.verify(Signature(t.sig.value), t.msg.value, PublicKey(t.vk.value)) match {
          case true  => Right(t)
          case false => Left(QuivrRuntimeErrors.ValidationError.MessageAuthorizationFailed(proof))
        }

    })

    val mapOfHashingRoutines: Map[String, DigestVerifier[Id]] = Map()

    override val datums = mapOfDatums.get _

    override val interfaces: Map[String, ParsableDataInterface] = mapOfInterfaces

    override val signingRoutines: Map[String, SignatureVerifier[Id]] = mapOfSigningRoutines

    override val hashingRoutines: Map[String, DigestVerifier[Id]] = mapOfHashingRoutines

    override def signableBytes: Id[SignableBytes] = "someSignableBytes".getBytes()

    override def currentTick: Id[Long] = 999

  }
}

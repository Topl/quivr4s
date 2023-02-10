package co.topl.quivr

import cats.Id
import cats.implicits._
import co.topl.common.ParsableDataInterface
import co.topl.crypto.PublicKey
import co.topl.crypto.signatures.Curve25519
import co.topl.crypto.signatures.Signature
import co.topl.quivr.algebras.DigestVerifier
import co.topl.quivr.algebras.SignatureVerifier
import co.topl.quivr.runtime.DynamicContext
import co.topl.quivr.runtime.QuivrRuntimeError
import co.topl.quivr.runtime.QuivrRuntimeErrors
import co.topl.brambl.models.Datum
import co.topl.brambl.models.Event
import co.topl.crypto.hash.blake2b256
import com.google.protobuf.ByteString
import quivr.models._

trait MockHelpers {

  val signableBytes = SignableBytes(ByteString.copyFromUtf8("someSignableBytes"))

  def dynamicContext(proposition: Proposition, proof: Proof): DynamicContext[Id, String, Datum] =
    new DynamicContext[Id, String, Datum] {

      private val mapOfDatums: Map[String, Datum] = Map("height" -> Datum().withHeader(Datum.Header(Event.Header(999))))

      private val mapOfInterfaces: Map[String, ParsableDataInterface] = Map()

      private val mapOfSigningRoutines: Map[String, SignatureVerifier[Id]] = Map(
        "Curve25519" -> new SignatureVerifier[Id] {

          override def validate(
            t: SignatureVerification
          ): Id[Either[QuivrRuntimeError, SignatureVerification]] =
            Curve25519.verify(
              Signature(t.signature.get.value.toByteArray),
              t.message.get.value.toByteArray,
              PublicKey(t.verificationKey.get.value.toByteArray)
            ) match {
              case true  => Right(t)
              case false => Left(QuivrRuntimeErrors.ValidationError.MessageAuthorizationFailed(proof))
            }

        }
      )

      private val mapOfHashingRoutines: Map[String, DigestVerifier[Id]] = Map("blake2b256" -> new DigestVerifier[Id] {

        override def validate(v: DigestVerification): Either[QuivrRuntimeError, DigestVerification] = {
          val test = blake2b256.hash(v.preimage.get.input.toByteArray ++ v.preimage.get.salt.toByteArray).value
          if (v.digest.get.value.digest32.get.value.toByteArray.sameElements(test)) Right(v)
          else Left(QuivrRuntimeErrors.ValidationError.LockedPropositionIsUnsatisfiable)
        }
      })

      override val datums = mapOfDatums.get _

      override val interfaces: Map[String, ParsableDataInterface] = mapOfInterfaces

      override val signingRoutines: Map[String, SignatureVerifier[Id]] = mapOfSigningRoutines

      override val hashingRoutines: Map[String, DigestVerifier[Id]] = mapOfHashingRoutines

      override def signableBytes: Id[SignableBytes] = SignableBytes(ByteString.copyFromUtf8("someSignableBytes"))

      override def currentTick: Id[Long] = 999

      override def heightOf(label: String): Id[Option[Long]] =
        mapOfDatums
          .get(label)
          .flatMap(_.value match {
            case Datum.Value.Header(Datum.Header(Event.Header(height, _), _)) => height.some
            case _                                                            => None
          })
    }
}

package co.topl.quivr

import cats.Id
import cats.implicits._
import co.topl.common.ParsableDataInterface
import co.topl.quivr.algebras.DigestVerifier
import co.topl.quivr.algebras.SignatureVerifier
import co.topl.quivr.runtime.DynamicContext
import co.topl.quivr.runtime.QuivrRuntimeError
import co.topl.quivr.runtime.QuivrRuntimeErrors
import co.topl.brambl.models.Datum
import co.topl.brambl.models.Event
import com.google.protobuf.ByteString
import quivr.models._

trait MockHelpers {

  val signableBytes = SignableBytes(ByteString.copyFromUtf8("someSignableBytes"))

  def dynamicContext(proposition: Proposition, proof: Proof): DynamicContext[Id, String, Datum] =
    new DynamicContext[Id, String, Datum] {

      private val mapOfDatums: Map[String, Datum] = Map("height" -> Datum().withHeader(Datum.Header(Event.Header(999))))

      private val mapOfInterfaces: Map[String, ParsableDataInterface] = Map()

      private val mapOfSigningRoutines: Map[String, SignatureVerifier[Id]] = Map(
        "VerySecure" -> new SignatureVerifier[Id] {

          override def validate(
            t: SignatureVerification
          ): Id[Either[QuivrRuntimeError, SignatureVerification]] =
            VerySecureSignatureRoutine.verify(
              t.signature.value.toByteArray,
              t.message.value.toByteArray,
              t.verificationKey.value.ed25519.get.value.toByteArray
            ) match {
              case true  => Right(t)
              case false => Left(QuivrRuntimeErrors.ValidationError.MessageAuthorizationFailed(proof))
            }

        }
      )

      private val mapOfHashingRoutines: Map[String, DigestVerifier[Id]] = Map("blake2b256" -> new DigestVerifier[Id] {

        override def validate(v: DigestVerification): Either[QuivrRuntimeError, DigestVerification] = {
          val test = co.topl.quivr.api.blake2b256Hash(v.preimage.input.toByteArray ++ v.preimage.salt.toByteArray)
          if (v.digest.value.digest32.get.value.toByteArray.sameElements(test)) Right(v)
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

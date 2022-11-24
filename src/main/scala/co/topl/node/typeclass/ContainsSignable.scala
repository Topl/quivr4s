package co.topl.node.typeclass

import co.topl.node.Tetra._
import co.topl.node._
import co.topl.node.outputs.{Blob, Box, IoTransaction, SpentOutput, UnspentOutput}
import co.topl.quivr.Models.Compositional.{And, Not, Or, Threshold}
import co.topl.quivr.Models.Contextual._
import co.topl.quivr.Models.Primitive.{Digest, DigitalSignature, Locked}
import co.topl.quivr.{Models, Proposition, SignableBytes}

import java.nio.charset.StandardCharsets

trait ContainsSignable[T] {
  def signableBytes(t: T): SignableBytes
}

object ContainsSignable {
  def apply[T](implicit ev: ContainsSignable[T]): ContainsSignable[T] = ev

  implicit class Ops[T: ContainsSignable](t: T) {
    def signable: SignableBytes = ContainsSignable[T].signableBytes(t)
  }

  trait Instances {

    implicit val ioTransactionSignable: ContainsSignable[IoTransaction] = (iotx: IoTransaction) =>
      iotx.inputs.zipWithIndex.foldLeft(Array[Byte]()) { case (acc, (stxo, idx)) =>
        acc ++ BigInt(idx).toByteArray ++ stxo.signable
      } ++
      iotx.outputs.zipWithIndex.foldLeft(Array[Byte]()) { case (acc, (utxo, idx)) =>
        acc ++ BigInt(idx).toByteArray ++ utxo.signable
      } ++
      iotx.datum.signable

    // doesn't include Box.Value since this is committed to via the reference
    implicit val spentOutputSignable: ContainsSignable[SpentOutput] = (stxo: SpentOutput) =>
      stxo.reference.bytes ++
      stxo.attestation.signable ++
      stxo.datum.signable

    implicit val predicateSignable: ContainsSignable[Predicate.Image] = (image: Predicate.Image) =>
      image.root ++ BigInt(image.threshold).toByteArray

    implicit val unspentOutputSignable: ContainsSignable[UnspentOutput] = (utxo: UnspentOutput) =>
      utxo.address.signable ++
      utxo.value.signable ++
      utxo.datum.signable

    implicit val boxSignable: ContainsSignable[Box] = (box: Box) =>
      box.image.root ++ BigInt(box.image.threshold).toByteArray

    implicit val boxValueSignable: ContainsSignable[Box.Value] = {
      case v: Box.Values.Token => tokenValueSignable.signableBytes(v)
      case v: Box.Values.Asset => assetValueSignable.signableBytes(v)
    }

    implicit val tokenValueSignable: ContainsSignable[Box.Values.Token] = (token: Box.Values.Token) =>
      BigInt(token.quantity).toByteArray

    implicit val assetValueSignable: ContainsSignable[Box.Values.Asset] = (asset: Box.Values.Asset) =>
      Array(asset.label) ++
      BigInt(asset.quantity).toByteArray ++
      asset.metadata

    implicit val addressSignable: ContainsSignable[Address] = (address: Address) =>
      BigInt(address.network).toByteArray ++
      BigInt(address.network).toByteArray ++
      address.evidence.bytes

    implicit val blobSignable: ContainsSignable[Blob] = (blob: Blob) => blob.value

    implicit val iotxScheduleSignable: ContainsSignable[IoTransaction.Schedule] = (schedule: IoTransaction.Schedule) =>
      BigInt(schedule.min).toByteArray ++
      BigInt(schedule.max).toByteArray

    implicit val iotxDatumSignable: ContainsSignable[Datums.IoTx] = (datum: Datums.IoTx) =>
      datum.schedule.signable ++
      datum.blobId.value ++
      datum.metadata

    implicit val stxoDatumSignable: ContainsSignable[Datums.SpentOutput] = (datum: Datums.SpentOutput) =>
      datum.blobId.value ++ datum.metadata

    implicit val utxoDatumSignable: ContainsSignable[Datums.UnspentOutput] = (datum: Datums.UnspentOutput) =>
      datum.blobId.value ++ datum.metadata

    implicit val attestationSignable: ContainsSignable[Attestation] = (attestation: Attestation) =>
      attestation.image.signable ++
      attestation.known.conditions.zipWithIndex.foldLeft(Array[Byte]()) {
        case (acc: Array[Byte], (Some(p: Proposition), index: Int)) =>
          acc ++
          BigInt(index).toByteArray ++ {
            p match {
              case _: Locked.Proposition => Models.Primitive.Locked.token.getBytes(StandardCharsets.UTF_8)
              case _: Digest.Proposition => Models.Primitive.Digest.token.getBytes(StandardCharsets.UTF_8)
              case _: DigitalSignature.Proposition =>
                Models.Primitive.DigitalSignature.token.getBytes(StandardCharsets.UTF_8)
              case _: HeightRange.Proposition => Models.Contextual.HeightRange.token.getBytes(StandardCharsets.UTF_8)
              case _: TickRange.Proposition   => Models.Contextual.TickRange.token.getBytes(StandardCharsets.UTF_8)
              case _: ExactMatch.Proposition  => Models.Contextual.ExactMatch.token.getBytes(StandardCharsets.UTF_8)
              case _: LessThan.Proposition    => Models.Contextual.LessThan.token.getBytes(StandardCharsets.UTF_8)
              case _: GreaterThan.Proposition => Models.Contextual.GreaterThan.token.getBytes(StandardCharsets.UTF_8)
              case _: EqualTo.Proposition     => Models.Contextual.EqualTo.token.getBytes(StandardCharsets.UTF_8)
              case _: Threshold.Proposition   => Models.Compositional.Threshold.token.getBytes(StandardCharsets.UTF_8)
              case _: Not.Proposition         => Models.Compositional.Not.token.getBytes(StandardCharsets.UTF_8)
              case _: And.Proposition         => Models.Compositional.And.token.getBytes(StandardCharsets.UTF_8)
              case _: Or.Proposition          => Models.Compositional.Or.token.getBytes(StandardCharsets.UTF_8)
              case _                          => Array.fill(8)(0xff.toByte) // 8 bytes of 1, should be eqv to -1L
            }
          }
      }
  }

  object instances extends Instances
}

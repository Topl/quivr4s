package co.topl.node.typeclasses

import co.topl.node._
import co.topl.node.transaction._
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

    implicit val predicateSignable: ContainsSignable[Predicate.Commitment] = (image: Predicate.Commitment) =>
      image.root ++ BigInt(image.threshold).toByteArray

    implicit val unspentOutputSignable: ContainsSignable[UnspentOutput] = (utxo: UnspentOutput) =>
      utxo.address.signable ++
      utxo.value.signable ++
      utxo.datum.signable

    implicit val boxSignable: ContainsSignable[Box] = (box: Box) =>
      box.image.signable ++
      box.value.signable

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

    implicit val iotxDatumSignable: ContainsSignable[TetraDatums.IoTx] = (datum: TetraDatums.IoTx) =>
      datum.schedule.signable ++
      datum.blobId.value ++
      datum.metadata

    implicit val stxoDatumSignable: ContainsSignable[TetraDatums.SpentOutput] = (datum: TetraDatums.SpentOutput) =>
      datum.blobId.value ++ datum.metadata

    implicit val utxoDatumSignable: ContainsSignable[TetraDatums.UnspentOutput] = (datum: TetraDatums.UnspentOutput) =>
      datum.blobId.value ++ datum.metadata

    // responses is not used when creating the signable bytes
    implicit val attestationSignable: ContainsSignable[Attestation] = (attestation: Attestation) =>
      attestation.image.signable ++
      attestation.known.challenges.zipWithIndex.foldLeft(Array[Byte]()) {
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

    private def lockedSignable(p: Locked.Proposition): SignableBytes =
      Locked.token.getBytes(StandardCharsets.UTF_8)

    private def digestSignable(p: Digest.Proposition): SignableBytes =
      Digest.token.getBytes(StandardCharsets.UTF_8) ++
      p.routine.getBytes(StandardCharsets.UTF_8) ++
      p.digest.value

    private def signatureSignable(p: DigitalSignature.Proposition): SignableBytes =
      DigitalSignature.token.getBytes(StandardCharsets.UTF_8) ++
      p.routine.getBytes(StandardCharsets.UTF_8) ++
      p.vk.value

    private def heightRangeSignable(p: HeightRange.Proposition): SignableBytes =
      HeightRange.token.getBytes(StandardCharsets.UTF_8) ++
      p.chain.getBytes(StandardCharsets.UTF_8) ++
      BigInt(p.min).toByteArray ++
      BigInt(p.max).toByteArray

    private def tickRangeSignable(p: TickRange.Proposition): SignableBytes =
      TickRange.token.getBytes(StandardCharsets.UTF_8) ++
      BigInt(p.min).toByteArray ++
      BigInt(p.max).toByteArray

    private def thresholdSignable(p: Threshold.Proposition): SignableBytes =
      Threshold.token.getBytes(StandardCharsets.UTF_8) ++
      BigInt(p.threshold).toByteArray ++
      p.challenges.zipWithIndex.foldLeft(Array[Byte]()) { case (acc, (challenge, index)) =>
        acc ++
        BigInt(index).toByteArray ++
        challenge.signable
      }

    private def notSignable(p: Not.Proposition): SignableBytes =
      Not.token.getBytes(StandardCharsets.UTF_8) ++
      p.proposition.signable

    private def andSignable(p: And.Proposition): SignableBytes =
      And.token.getBytes(StandardCharsets.UTF_8) ++
      p.left.signable ++
      p.right.signable

    private def orSignable(p: Or.Proposition): SignableBytes =
      Or.token.getBytes(StandardCharsets.UTF_8) ++
      p.left.signable ++
      p.right.signable

    implicit val propositionSignable: ContainsSignable[Proposition] = {
      case p: Locked.Proposition           => lockedSignable(p)
      case p: Digest.Proposition           => digestSignable(p)
      case p: DigitalSignature.Proposition => signatureSignable(p)
      case p: HeightRange.Proposition      => heightRangeSignable(p)
      case p: TickRange.Proposition        => tickRangeSignable(p)
      case p: ExactMatch.Proposition       => ???
      case p: LessThan.Proposition         => ???
      case p: GreaterThan.Proposition      => ???
      case p: EqualTo.Proposition          => ???
      case p: Threshold.Proposition        => thresholdSignable(p)
      case p: Not.Proposition              => notSignable(p)
      case p: And.Proposition              => andSignable(p)
      case p: Or.Proposition               => orSignable(p)
    }
  }

  object instances extends Instances
}

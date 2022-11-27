package co.topl.node.typeclasses

import co.topl.node._
import co.topl.node.box.{Blob, Box, Lock, Locks, Value, Values}
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

    implicit val byteSingable: ContainsSignable[Byte] = (byte: Byte) => Array(byte)

    implicit val arrayByteSignable: ContainsSignable[Array[Byte]] = (bytes: Array[Byte]) => bytes

    implicit val intSignable: ContainsSignable[Int] = (int: Int) => BigInt(int).toByteArray

    implicit val longSignable: ContainsSignable[Long] = (long: Long) => BigInt(long).toByteArray

    implicit val stringSignable: ContainsSignable[String] = (string: String) => string.getBytes(StandardCharsets.UTF_8)

    implicit def listSignable[T: ContainsSignable]: ContainsSignable[List[T]] = (list: List[T]) =>
      list.zipWithIndex.foldLeft(Array[Byte]()) { case (acc, (item, index)) =>
        acc ++ BigInt(index).toByteArray ++ item.signable
      }

    implicit def optionSignable[T: ContainsSignable]: ContainsSignable[Option[T]] = (option: Option[T]) =>
      option.fold(Array(0xff.toByte))(_.signable)

    implicit val evidenceSignable: ContainsSignable[Evidence] = (evidence: Evidence) => evidence.value

    implicit val identifiersSignable: ContainsSignable[Identifier] = {
      case i: Identifiers.BoxLock       => boxLockIdentifierSignable.signableBytes(i)
      case i: Identifiers.BoxValue      => boxValueIdentifierSignable.signableBytes(i)
      case i: Identifiers.IoTransaction => ioTransactionIdentifierSignable.signableBytes(i)
    }

    implicit val boxLockIdentifierSignable: ContainsSignable[Identifiers.BoxLock] = (id: Identifiers.BoxLock) =>
      id.tag.signable ++
      id.value.signable

    implicit val boxValueIdentifierSignable: ContainsSignable[Identifiers.BoxValue] = (id: Identifiers.BoxValue) =>
      id.tag.signable ++
      id.value.signable

    implicit val ioTransactionIdentifierSignable: ContainsSignable[Identifiers.IoTransaction] =
      (id: Identifiers.IoTransaction) =>
        id.tag.signable ++
        id.value.signable

    implicit val lockSignable: ContainsSignable[Lock] = {
      case l: Locks.Predicate  => predicateLockSignable.signableBytes(l)
      case l: Locks.Image      => imageLockSignable.signableBytes(l)
      case l: Locks.Commitment => commitmentLockSignable.signableBytes(l)
    }

    implicit val boxValueSignable: ContainsSignable[Value] = {
      case v: Values.Token => tokenValueSignable.signableBytes(v)
      case v: Values.Asset => assetValueSignable.signableBytes(v)
    }

    implicit val referenceSignable: ContainsSignable[Reference] = {
      case r: References.KnownPredicate => knownPredicateReferenceSignable.signableBytes(r)
      case r: References.Blob           => blobReferenceSignable.signableBytes(r)
      case r: References.Output         => outputReferenceSignable.signableBytes(r)
    }

    implicit val knownPredicateReferenceSignable: ContainsSignable[References.KnownPredicate] =
      (reference: References.KnownPredicate) =>
        reference.index.signable ++
        reference.id.signable

    implicit val blobReferenceSignable: ContainsSignable[References.Blob] =
      (reference: References.Blob) =>
        reference.index.signable ++
        reference.id.signable

    implicit val outputReferenceSignable: ContainsSignable[References.Output] =
      (reference: References.Output) =>
        reference.index.signable ++
        reference.id.signable

    // responses is not used when creating the signable bytes
    implicit val attestationSignable: ContainsSignable[Attestation] = {
      case a: Attestations.Predicate  => predicateAttestationSignable.signableBytes(a)
      case a: Attestations.Image      => imageAttestationSignable.signableBytes(a)
      case a: Attestations.Commitment => commitmentAttestationSignable.signableBytes(a)
    }

    implicit val ioTransactionSignable: ContainsSignable[IoTransaction] = (iotx: IoTransaction) =>
      iotx.inputs.signable ++
      iotx.outputs.signable ++
      iotx.datum.signable

    // doesn't include Box.Value since this is committed to via the reference
    implicit val spentOutputSignable: ContainsSignable[Outputs.Spent] = (stxo: Outputs.Spent) =>
      stxo.reference.signable ++
      stxo.attestation.signable ++
      stxo.datum.signable

    implicit val unspentOutputSignable: ContainsSignable[Outputs.Unspent] = (utxo: Outputs.Unspent) =>
      utxo.address.signable ++
      utxo.value.signable ++
      utxo.datum.signable

    // consider making predicate non-empty
    implicit val predicateLockSignable: ContainsSignable[Locks.Predicate] = (predicate: Locks.Predicate) =>
      predicate.threshold.signable ++
      predicate.challenges.signable

    implicit val imageLockSignable: ContainsSignable[Locks.Image] = (image: Locks.Image) =>
      image.threshold.signable ++
      image.leaves.signable

    implicit val commitmentLockSignable: ContainsSignable[Locks.Commitment] = (commitment: Locks.Commitment) =>
      commitment.threshold.signable ++
      commitment.size.signable ++
      commitment.root.signable

    implicit val boxSignable: ContainsSignable[Box] = (box: Box) =>
      box.lock.signable ++
      box.value.signable

    implicit val tokenValueSignable: ContainsSignable[Values.Token] = (token: Values.Token) =>
      token.quantity.signable ++
      token.blobs.signable

    implicit val assetValueSignable: ContainsSignable[Values.Asset] = (asset: Values.Asset) =>
      asset.label.signable ++
      asset.quantity.signable ++
      asset.metadata.signable

    implicit val addressSignable: ContainsSignable[Address] = (address: Address) =>
      address.network.signable ++
      address.ledger.signable ++
      address.reference.signable

    implicit val blobSignable: ContainsSignable[Blob] = (blob: Blob) => blob.value.signable

    implicit val iotxScheduleSignable: ContainsSignable[IoTransaction.Schedule] = (schedule: IoTransaction.Schedule) =>
      schedule.min.signable ++
      schedule.max.signable

    implicit val iotxDatumSignable: ContainsSignable[Datums.IoTx] = (datum: Datums.IoTx) =>
      datum.schedule.signable ++
      datum.references.signable ++
      datum.metadata.signable

    implicit val stxoDatumSignable: ContainsSignable[Datums.SpentOutput] = (datum: Datums.SpentOutput) =>
      datum.references.signable ++
      datum.metadata.signable

    implicit val utxoDatumSignable: ContainsSignable[Datums.UnspentOutput] = (datum: Datums.UnspentOutput) =>
      datum.references.signable ++
      datum.metadata.signable

    implicit val predicateAttestationSignable: ContainsSignable[Attestations.Predicate] =
      (attestation: Attestations.Predicate) => attestation.lock.signable

    implicit val imageAttestationSignable: ContainsSignable[Attestations.Image] = (attestation: Attestations.Image) =>
      attestation.lock.signable ++
      attestation.known.signable

    implicit val commitmentAttestationSignable: ContainsSignable[Attestations.Commitment] =
      (attestation: Attestations.Commitment) =>
        attestation.lock.signable ++
        attestation.known.signable

    implicit val lockedSignable: ContainsSignable[Models.Primitive.Locked.Proposition] = (p: Locked.Proposition) =>
      Locked.token.signable

    implicit val digestSignable: ContainsSignable[Models.Primitive.Digest.Proposition] = (p: Digest.Proposition) =>
      Digest.token.signable ++
      p.routine.signable ++
      p.digest.value.signable

    implicit val signatureSignable: ContainsSignable[Models.Primitive.DigitalSignature.Proposition] =
      (p: DigitalSignature.Proposition) =>
        DigitalSignature.token.getBytes(StandardCharsets.UTF_8) ++
        p.routine.getBytes(StandardCharsets.UTF_8) ++
        p.vk.value

    implicit val heightRangeSignable: ContainsSignable[Models.Contextual.HeightRange.Proposition] =
      (p: HeightRange.Proposition) =>
        HeightRange.token.signable ++
        p.chain.signable ++
        p.min.signable ++
        p.max.signable

    implicit val tickRangeSignable: ContainsSignable[Models.Contextual.TickRange.Proposition] =
      (p: TickRange.Proposition) =>
        TickRange.token.signable ++
        p.min.signable ++
        p.max.signable

    implicit val thresholdSignable: ContainsSignable[Models.Compositional.Threshold.Proposition] =
      (p: Threshold.Proposition) =>
        Threshold.token.signable ++
        p.threshold.signable ++
        p.challenges.toList.signable

    implicit val notSignable: ContainsSignable[Models.Compositional.Not.Proposition] = (p: Not.Proposition) =>
      Not.token.signable ++
      p.proposition.signable

    implicit val andSignable: ContainsSignable[Models.Compositional.And.Proposition] = (p: And.Proposition) =>
      And.token.signable ++
      p.left.signable ++
      p.right.signable

    implicit val orSignable: ContainsSignable[Models.Compositional.Or.Proposition] = (p: Or.Proposition) =>
      Or.token.signable ++
      p.left.signable ++
      p.right.signable

    implicit val propositionSignable: ContainsSignable[Proposition] = {
      case p: Locked.Proposition           => lockedSignable.signableBytes(p)
      case p: Digest.Proposition           => digestSignable.signableBytes(p)
      case p: DigitalSignature.Proposition => signatureSignable.signableBytes(p)
      case p: HeightRange.Proposition      => heightRangeSignable.signableBytes(p)
      case p: TickRange.Proposition        => tickRangeSignable.signableBytes(p)
      case p: ExactMatch.Proposition       => ???
      case p: LessThan.Proposition         => ???
      case p: GreaterThan.Proposition      => ???
      case p: EqualTo.Proposition          => ???
      case p: Threshold.Proposition        => thresholdSignable.signableBytes(p)
      case p: Not.Proposition              => notSignable.signableBytes(p)
      case p: And.Proposition              => andSignable.signableBytes(p)
      case p: Or.Proposition               => orSignable.signableBytes(p)
    }
  }

  object instances extends Instances
}

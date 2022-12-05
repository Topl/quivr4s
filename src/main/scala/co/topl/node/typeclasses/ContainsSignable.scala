package co.topl.node.typeclasses

import co.topl.node._
import co.topl.node.box._
import co.topl.node.transaction._
import co.topl.quivr.Models.Compositional.{And, Not, Or, Threshold}
import co.topl.quivr.Models.Contextual._
import co.topl.quivr.Models.Primitive.{Digest, DigitalSignature, Locked}
import co.topl.quivr.runtime.Datum
import co.topl.quivr.{Models, Proof, Proposition, SignableBytes}

import java.nio.charset.StandardCharsets
// Long -> longSignable -> longSignableEvidence -> longSignableEvidenceId
// Long -> longSignable -> longSignableEvidence -> longSingableEvidenceSignable -> longSingableEvidenceSignableEvidence
// Object -> Signable -> Evidence -> Identifier -> Address -> KnownIdentifier

// Topl: TObject -> TSignable -> TEvidence -> TIdentifier -> TAddress -> TKnownIdentifier
// DAML: DObject -> DSignable -> DEvidence (==TEvidence) -> TSignable -> TEvidence -> TIdentifier -> TAddress -> TKnownIdentifier
trait ContainsSignable[T] {
  def signableBytes(t: T): SignableBytes
}

object ContainsSignable {
  def apply[T](implicit ev: ContainsSignable[T]): ContainsSignable[T] = ev

  implicit class Ops[T: ContainsSignable](t: T) {
    def signable: SignableBytes = ContainsSignable[T].signableBytes(t)
  }

  trait Instances {

    implicit val byteSingable: ContainsSignable[Byte] = (t: Byte) => Array(t)

    implicit val arrayByteSignable: ContainsSignable[Array[Byte]] = (bytes: Array[Byte]) => bytes

    implicit val intSignable: ContainsSignable[Int] = (int: Int) => BigInt(int).toByteArray

    implicit val longSignable: ContainsSignable[Long] = (long: Long) => BigInt(long).toByteArray

    implicit val stringSignable: ContainsSignable[String] = (string: String) => string.getBytes(StandardCharsets.UTF_8)

    implicit def listSignable[T: ContainsSignable]: ContainsSignable[List[T]] = (list: List[T]) =>
      list.zipWithIndex.foldLeft(Array[Byte]()) { case (acc, (item, index)) =>
        acc.signable ++
        index.signable ++
        item.signable
      }

    implicit def optionSignable[T: ContainsSignable]: ContainsSignable[Option[T]] = (option: Option[T]) =>
      option.fold(Array(0xff.toByte))(_.signable)

    implicit def EventSignable[T <: Event: ContainsSignable]: ContainsSignable[Datum[T]] = (datum: Datum[T]) =>
      datum.event.signable

    implicit def referenceSignable[T: ContainsSignable]: ContainsSignable[Reference[T]] =
      (reference: Reference[T]) =>
        reference.datum.signable ++
        reference.opts.signable

    implicit val blobSignable: ContainsSignable[Blob] = (blob: Blob) => blob.value.signable

    implicit val ioTransactionSignable: ContainsSignable[IoTransaction] = (iotx: IoTransaction) =>
      iotx.inputs.signable ++
      iotx.outputs.signable ++
      iotx.datum.signable

    implicit val iotxScheduleSignable: ContainsSignable[IoTransaction.Schedule] = (schedule: IoTransaction.Schedule) =>
      schedule.min.signable ++
      schedule.max.signable

    implicit val spentOutputSignable: ContainsSignable[SpentTransactionOutput] = (stxo: SpentTransactionOutput) =>
      stxo.knownIdentifier.signable ++
      stxo.attestation.signable ++
      stxo.value.signable ++
      stxo.datum.signable

    implicit val unspentOutputSignable: ContainsSignable[UnspentTransactionOutput] = (utxo: UnspentTransactionOutput) =>
      utxo.address.signable ++
      utxo.value.signable ++
      utxo.datum.signable

    implicit val boxSignable: ContainsSignable[Box] = (box: Box) =>
      box.lock.signable ++
      box.value.signable

    implicit val addressSignable: ContainsSignable[Address] = (address: Address) =>
      address.network.signable ++
      address.ledger.signable ++
      address.identifier.signable

    implicit val evidenceSignable: ContainsSignable[Evidence[_]] = {
      case e: Evidence.Sized32 => size32EvidenceSignable.signableBytes(e)
      case e: Evidence.Sized64 => size64EvidenceSignable.signableBytes(e)
    }

    implicit val size32EvidenceSignable: ContainsSignable[Evidence.Sized32] = (ev: Evidence.Sized32) => ev.digest.value

    implicit val size64EvidenceSignable: ContainsSignable[Evidence.Sized64] = (ev: Evidence.Sized64) => ev.digest.value

    implicit val identifiersSignable: ContainsSignable[Identifier] = {
      case i: Identifiers.AccumulatorRoot32 => accumulatorRoot32IdentifierSignable.signableBytes(i)
      case i: Identifiers.AccumulatorRoot64 => accumulatorRoot64IdentifierSignable.signableBytes(i)
      case i: Identifiers.Lock32            => boxLock32IdentifierSignable.signableBytes(i)
      case i: Identifiers.Lock64            => boxLock64IdentifierSignable.signableBytes(i)
      case i: Identifiers.BoxValue32        => boxValue32IdentifierSignable.signableBytes(i)
      case i: Identifiers.BoxValue64        => boxValue64IdentifierSignable.signableBytes(i)
      case i: Identifiers.IoTransaction32   => ioTransaction32IdentifierSignable.signableBytes(i)
      case i: Identifiers.IoTransaction64   => ioTransaction64IdentifierSignable.signableBytes(i)
    }

    implicit val accumulatorRoot32IdentifierSignable: ContainsSignable[Identifiers.AccumulatorRoot32] =
      (id: Identifiers.AccumulatorRoot32) =>
        id.tag.signable ++
        id.evidence.value.signable

    implicit val accumulatorRoot64IdentifierSignable: ContainsSignable[Identifiers.AccumulatorRoot64] =
      (id: Identifiers.AccumulatorRoot64) =>
        id.tag.signable ++
        id.evidence.value.signable

    implicit val boxLock32IdentifierSignable: ContainsSignable[Identifiers.Lock32] =
      (id: Identifiers.Lock32) =>
        id.tag.signable ++
        id.evidence.value.signable

    implicit val boxLock64IdentifierSignable: ContainsSignable[Identifiers.Lock64] =
      (id: Identifiers.Lock64) =>
        id.tag.signable ++
        id.evidence.value.signable

    implicit val boxValue32IdentifierSignable: ContainsSignable[Identifiers.BoxValue32] =
      (id: Identifiers.BoxValue32) =>
        id.tag.signable ++
        id.evidence.value.signable

    implicit val boxValue64IdentifierSignable: ContainsSignable[Identifiers.BoxValue64] =
      (id: Identifiers.BoxValue64) =>
        id.tag.signable ++
        id.evidence.value.signable

    implicit val ioTransaction32IdentifierSignable: ContainsSignable[Identifiers.IoTransaction32] =
      (id: Identifiers.IoTransaction32) =>
        id.tag.signable ++
        id.evidence.value.signable

    implicit val ioTransaction64IdentifierSignable: ContainsSignable[Identifiers.IoTransaction64] =
      (id: Identifiers.IoTransaction64) =>
        id.tag.signable ++
        id.evidence.value.signable

    implicit val knownIdentifierSignable: ContainsSignable[KnownIdentifier] = {
      case r: KnownIdentifiers.TransactionOutput32 => knownOutput32IdentifierSignable.signableBytes(r)
      case r: KnownIdentifiers.TransactionOutput64 => knownOutput64IdentifierSignable.signableBytes(r)
    }

    implicit val knownOutput32IdentifierSignable: ContainsSignable[KnownIdentifiers.TransactionOutput32] =
      (knownId: KnownIdentifiers.TransactionOutput32) =>
        knownId.network.signable ++
        knownId.ledger.signable ++
        knownId.index.signable ++
        knownId.id.signable

    implicit val knownOutput64IdentifierSignable: ContainsSignable[KnownIdentifiers.TransactionOutput64] =
      (knownId: KnownIdentifiers.TransactionOutput64) =>
        knownId.network.signable ++
        knownId.ledger.signable ++
        knownId.index.signable ++
        knownId.id.signable

    implicit def knownReferenceSignable[T: ContainsSignable]: ContainsSignable[KnownReference[T]] = {
      case k: KnownReferences.Predicate => knownPredicateReferenceSignable.signableBytes(k)
      case k: KnownReferences.Blob      => knownBlobReferenceSignable.signableBytes(k)
      case k: KnownReferences.Leaf      => knownLeafReferenceSignable.signableBytes(k)
    }

    implicit val knownPredicateReferenceSignable: ContainsSignable[KnownReferences.Predicate] =
      (known: KnownReferences.Predicate) =>
        known.network.signable ++
        known.ledger.signable ++
        known.indices.signable ++
        known.reference.signable

    implicit val knownBlobReferenceSignable: ContainsSignable[KnownReferences.Blob] =
      (known: KnownReferences.Blob) =>
        known.network.signable ++
        known.ledger.signable ++
        known.indices.signable ++
        known.reference.signable

    implicit val knownLeafReferenceSignable: ContainsSignable[KnownReferences.Leaf] =
      (known: KnownReferences.Leaf) =>
        known.network.signable ++
        known.ledger.signable ++
        known.indices.signable ++
        known.reference.signable

    implicit val boxValueSignable: ContainsSignable[Value] = {
      case v: Values.Token => tokenValueSignable.signableBytes(v)
      case v: Values.Asset => assetValueSignable.signableBytes(v)
    }

    implicit val tokenValueSignable: ContainsSignable[Values.Token] = (token: Values.Token) =>
      token.quantity.signable

    implicit val assetValueSignable: ContainsSignable[Values.Asset] = (asset: Values.Asset) =>
      asset.label.signable ++
      asset.quantity.signable ++
      asset.metadata.signable

    implicit val lockSignable: ContainsSignable[Lock] = {
      case l: Locks.Predicate    => predicateLockSignable.signableBytes(l)
      case l: Locks.Image32      => image32LockSignable.signableBytes(l)
      case l: Locks.Image64      => image64LockSignable.signableBytes(l)
      case l: Locks.Commitment32 => commitment32LockSignable.signableBytes(l)
      case l: Locks.Commitment64 => commitment64LockSignable.signableBytes(l)
    }

    // consider making predicate non-empty
    implicit val predicateLockSignable: ContainsSignable[Locks.Predicate] = (predicate: Locks.Predicate) =>
      predicate.threshold.signable ++
      predicate.challenges.signable

    implicit val image32LockSignable: ContainsSignable[Locks.Image32] = (image: Locks.Image32) =>
      image.threshold.signable ++
      image.leaves.signable

    implicit val image64LockSignable: ContainsSignable[Locks.Image64] = (image: Locks.Image64) =>
      image.threshold.signable ++
      image.leaves.signable

    implicit val commitment32LockSignable: ContainsSignable[Locks.Commitment32] = (commitment: Locks.Commitment32) =>
      commitment.threshold.signable ++
      commitment.size.signable ++
      commitment.root.signable

    implicit val commitment64LockSignable: ContainsSignable[Locks.Commitment64] = (commitment: Locks.Commitment64) =>
      commitment.threshold.signable ++
      commitment.size.signable ++
      commitment.root.signable

    // responses is not used when creating the signable bytes
    implicit val attestationSignable: ContainsSignable[Attestation] = {
      case a: Attestations.Predicate    => predicateAttestationSignable.signableBytes(a)
      case a: Attestations.Image32      => image32AttestationSignable.signableBytes(a)
      case a: Attestations.Image64      => image64AttestationSignable.signableBytes(a)
      case a: Attestations.Commitment32 => commitment32AttestationSignable.signableBytes(a)
      case a: Attestations.Commitment64 => commitment64AttestationSignable.signableBytes(a)
    }

    implicit val predicateAttestationSignable: ContainsSignable[Attestations.Predicate] =
      (attestation: Attestations.Predicate) =>
        attestation.lock.signable ++
        attestation.responses.signable

    implicit val image32AttestationSignable: ContainsSignable[Attestations.Image32] =
      (attestation: Attestations.Image32) =>
        attestation.lock.signable ++
        attestation.known.signable ++
        attestation.responses.signable

    implicit val image64AttestationSignable: ContainsSignable[Attestations.Image64] =
      (attestation: Attestations.Image64) =>
        attestation.lock.signable ++
        attestation.known.signable ++
        attestation.responses.signable

    implicit val commitment32AttestationSignable: ContainsSignable[Attestations.Commitment32] =
      (attestation: Attestations.Commitment32) =>
        attestation.lock.signable ++
        attestation.known.signable ++
        attestation.responses.signable

    implicit val commitment64AttestationSignable: ContainsSignable[Attestations.Commitment64] =
      (attestation: Attestations.Commitment64) =>
        attestation.lock.signable ++
        attestation.known.signable ++
        attestation.responses.signable

    implicit val eventSignable: ContainsSignable[Event] = {
      case e: Events.Eon                      => eonEventSignable.signableBytes(e)
      case e: Events.Era                      => eraEventSignable.signableBytes(e)
      case e: Events.Epoch                    => epochEventSignable.signableBytes(e)
      case e: Events.Header                   => headerEventSignable.signableBytes(e)
      case e: Events.Root                     => rootEventSignable.signableBytes(e)
      case e: Events.IoTransaction            => iotxEventSignable.signableBytes(e)
      case e: Events.SpentTransactionOutput   => stxoEventSignable.signableBytes(e)
      case e: Events.UnspentTransactionOutput => utxoEventSignable.signableBytes(e)
    }

    implicit val eonEventSignable: ContainsSignable[Events.Eon] =
      (event: Events.Eon) =>
        event.beginSlot.signable ++
        event.height.signable

    implicit val eraEventSignable: ContainsSignable[Events.Era] =
      (event: Events.Era) =>
        event.beginSlot.signable ++
        event.height.signable

    implicit val epochEventSignable: ContainsSignable[Events.Epoch] =
      (event: Events.Epoch) =>
        event.beginSlot.signable ++
        event.height.signable

    implicit val headerEventSignable: ContainsSignable[Events.Header] =
      (event: Events.Header) => event.height.signable

    implicit val rootEventSignable: ContainsSignable[Events.Root] =
      (event: Events.Root) => event.value.signable

    implicit val iotxEventSignable: ContainsSignable[Events.IoTransaction] = (event: Events.IoTransaction) =>
      event.schedule.signable ++
      event.references32.signable ++
      event.metadata.signable

    implicit val stxoEventSignable: ContainsSignable[Events.SpentTransactionOutput] =
      (event: Events.SpentTransactionOutput) =>
        event.references.signable ++
        event.metadata.signable

    implicit val utxoEventSignable: ContainsSignable[Events.UnspentTransactionOutput] =
      (event: Events.UnspentTransactionOutput) =>
        event.references.signable ++
        event.metadata.signable

    implicit val proofSignable: ContainsSignable[Proof] = {
      case _: Locked.Proof           => Array(0xff.toByte)
      case _: Digest.Proof           => Array(0xff.toByte)
      case _: DigitalSignature.Proof => Array(0xff.toByte)
      case _: HeightRange.Proof      => Array(0xff.toByte)
      case _: TickRange.Proof        => Array(0xff.toByte)
      case _: ExactMatch.Proof       => Array(0xff.toByte)
      case _: LessThan.Proof         => Array(0xff.toByte)
      case _: GreaterThan.Proof      => Array(0xff.toByte)
      case _: EqualTo.Proof          => Array(0xff.toByte)
      case _: Threshold.Proof        => Array(0xff.toByte)
      case _: Not.Proof              => Array(0xff.toByte)
      case _: And.Proof              => Array(0xff.toByte)
      case _: Or.Proof               => Array(0xff.toByte)
    }

    implicit val propositionSignable: ContainsSignable[Proposition] = {
      case p: Locked.Proposition           => lockedSignable.signableBytes(p)
      case p: Digest.Proposition           => digestSignable.signableBytes(p)
      case p: DigitalSignature.Proposition => signatureSignable.signableBytes(p)
      case p: HeightRange.Proposition      => heightRangeSignable.signableBytes(p)
      case p: TickRange.Proposition        => tickRangeSignable.signableBytes(p)
      case p: ExactMatch.Proposition       => exactMatchSignable.signableBytes(p)
      case p: LessThan.Proposition         => lessThanSignable.signableBytes(p)
      case p: GreaterThan.Proposition      => greaterThanSignable.signableBytes(p)
      case p: EqualTo.Proposition          => equalToSignable.signableBytes(p)
      case p: Threshold.Proposition        => thresholdSignable.signableBytes(p)
      case p: Not.Proposition              => notSignable.signableBytes(p)
      case p: And.Proposition              => andSignable.signableBytes(p)
      case p: Or.Proposition               => orSignable.signableBytes(p)
    }

    implicit val lockedSignable: ContainsSignable[Models.Primitive.Locked.Proposition] = (p: Locked.Proposition) =>
      Locked.token.signable

    implicit val digestSignable: ContainsSignable[Models.Primitive.Digest.Proposition] = (p: Digest.Proposition) =>
      Digest.token.signable ++
      p.routine.signable ++
      p.digest.value.signable

    implicit val signatureSignable: ContainsSignable[Models.Primitive.DigitalSignature.Proposition] =
      (p: DigitalSignature.Proposition) =>
        DigitalSignature.token.signable ++
        p.routine.signable ++
        p.vk.value.signable

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

    implicit val exactMatchSignable: ContainsSignable[Models.Contextual.ExactMatch.Proposition] =
      (p: ExactMatch.Proposition) =>
        ExactMatch.token.signable ++
        p.location.signable ++
        p.compareTo.signable

    implicit val lessThanSignable: ContainsSignable[Models.Contextual.LessThan.Proposition] =
      (p: LessThan.Proposition) =>
        LessThan.token.signable ++
        p.location.signable ++
        p.compareTo.signable

    implicit val greaterThanSignable: ContainsSignable[Models.Contextual.GreaterThan.Proposition] =
      (p: GreaterThan.Proposition) =>
        GreaterThan.token.signable ++
        p.location.signable ++
        p.compareTo.signable

    implicit val equalToSignable: ContainsSignable[Models.Contextual.EqualTo.Proposition] =
      (p: EqualTo.Proposition) =>
        EqualTo.token.signable ++
        p.location.signable ++
        p.compareTo.signable

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
  }

  object instances extends Instances
}

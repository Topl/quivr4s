package co.topl.brambl.wallet

import co.topl.brambl.Models.{Indices, KeyPair, SigningKey}
import co.topl.brambl.digests.Blake2b256Digest
import co.topl.brambl.signatures.{Curve25519Signature, Signing}
import co.topl.brambl.{Context, QuivrService}
import co.topl.common.Models.{Preimage, VerificationKey}
import co.topl.node.box.{Box, Locks, Values}
import co.topl.node.transaction.IoTransaction.Schedule
import co.topl.node.transaction.{Datums, IoTransaction}
import co.topl.node.{Address, Events, Identifiers, KnownIdentifier, KnownIdentifiers}

// Wallet storage api. Will just return dummy values

// TODO: Make a map of Address => KnownId and KnownId => Box. Each address will have a different lock

object MockStorage extends IStorage {

  // Arbitrary Transaction that any new transaction can reference
  private val dummyTx2a = IoTransaction(List(), List(),
    Datums.ioTransactionDatum(Events.IoTransaction(Schedule(1, 5, 100), List(), List(), Array())))
  private val dummyTx2b = IoTransaction(List(), List(),
    Datums.ioTransactionDatum(Events.IoTransaction(Schedule(2, 50, 100), List(), List(), Array())))
  private val dummyTx3 = IoTransaction(List(), List(),
    Datums.ioTransactionDatum(Events.IoTransaction(Schedule(3, 50, 100), List(), List(), Array())))
  private val dummyTx4 = IoTransaction(List(), List(),
    Datums.ioTransactionDatum(Events.IoTransaction(Schedule(4, 50, 100), List(), List(), Array())))
  private val dummyTx5 = IoTransaction(List(), List(),
    Datums.ioTransactionDatum(Events.IoTransaction(Schedule(5, 50, 100), List(), List(), Array())))


  private val dummyTxIdentifier2a = KnownIdentifiers.TransactionOutput32(0, 0, 0, Identifiers.transaction32(dummyTx2a))
  private val dummyTxIdentifier2b = KnownIdentifiers.TransactionOutput32(0, 0, 0, Identifiers.transaction32(dummyTx2b))
  private val dummyTxIdentifier3 = KnownIdentifiers.TransactionOutput32(0, 0, 0, Identifiers.transaction32(dummyTx3))
  private val dummyTxIdentifier4 = KnownIdentifiers.TransactionOutput32(0, 0, 0, Identifiers.transaction32(dummyTx4))
  private val dummyTxIdentifier5 = KnownIdentifiers.TransactionOutput32(0, 0, 0, Identifiers.transaction32(dummyTx5))


  // Static mappings to provide the Wallet with data

  val idxToLocks: Map[Indices, Locks.Predicate] = Map(
    Indices(1, 2, 0) -> buildPredicate(2, Indices(1, 2, 0)),
    Indices(0, 2, 1) -> buildPredicate(2, Indices(0, 2, 1)),
    Indices(0, 3, 0) -> buildPredicate(3, Indices(0, 3, 0)),
    Indices(1, 4, 0) -> buildPredicate(4, Indices(1, 4, 0)),
    Indices(1, 5, 0) -> buildPredicate(5, Indices(1, 5, 0)),
  )


  val addr2a: Address = Address(0, 0, Identifiers.boxLock32(idxToLocks(Indices(1, 2, 0))))
  val addr2b: Address = Address(0, 0, Identifiers.boxLock32(idxToLocks(Indices(0, 2, 1))))
  val addr3: Address = Address(0, 0, Identifiers.boxLock32(idxToLocks(Indices(0, 3, 0))))
  val addr4: Address = Address(0, 0, Identifiers.boxLock32(idxToLocks(Indices(1, 4, 0))))
  val addr5: Address = Address(0, 0, Identifiers.boxLock32(idxToLocks(Indices(1, 5, 0))))

  val idToIdx: Map[KnownIdentifier, Indices] = Map(
    dummyTxIdentifier2a -> Indices(1, 2, 0), // with data
    dummyTxIdentifier2b -> Indices(0, 2, 1), // without data
    dummyTxIdentifier3 -> Indices(0, 3, 0), // without data
    dummyTxIdentifier4 -> Indices(1, 4, 0), // with data
    dummyTxIdentifier5 -> Indices(1, 5, 0) // with data
  )

  val addrToId: Map[Address, KnownIdentifier] = Map(
    addr2a -> dummyTxIdentifier2a,
    addr2b -> dummyTxIdentifier2b,
    addr3 -> dummyTxIdentifier3,
    addr4 -> dummyTxIdentifier4,
    addr5 -> dummyTxIdentifier5,
  )

  // Hardcoding MockStorage to use Blake2b256Digest and Curve25519Signature
  private def buildPredicate(threshold: Int, idx: Indices): Locks.Predicate = Locks.Predicate(
    List(
      QuivrService.lockedProposition.get,
      QuivrService.digestProposition(
        getPreimage(idx).getOrElse(Preimage("unsolvable preimage".getBytes, "salt".getBytes)),
        Blake2b256Digest
      ).get,
      QuivrService.signatureProposition(
        getKeyPair(idx, Curve25519Signature)
        .getOrElse(KeyPair(SigningKey("fake sk".getBytes), VerificationKey("fake vk".getBytes))).vk,
        Curve25519Signature
      ).get,
      QuivrService.heightProposition(2, 8).get,
      QuivrService.tickProposition(2, 8).get,
    ),
    threshold // N of 5 predicate
  )


  private def getSecret(idx: Indices): Array[Byte] = s"${idx.x},${idx.y},${idx.z}".getBytes

  override def getIndicesByIdentifier(id: KnownIdentifier): Option[Indices] = idToIdx.get(id)
  override def getKnownIdentifierByAddress(address: Address): Option[KnownIdentifier] = addrToId.get(address)

  override def getBoxByKnownIdentifier(id: KnownIdentifier): Option[Box] = idToIdx.get(id)
    .flatMap(idxToLocks.get)
    .map(Box(_, Values.Token(1, List())))

  override def getPreimage(idx: Indices): Option[Preimage] =
    if(idx.x == 1) // Mocking that we only have access to secrets associated with x=1
        Some(Preimage(getSecret(idx), "salt".getBytes))
    else None
  override def getKeyPair(idx: Indices, routine: Signing): Option[KeyPair] =
    if(idx.x == 1){ // Mocking that we only have access to secrets associated with x=1
      Some(routine.createKeyPair(getSecret(idx)))
    } else None

}
package co.topl.brambl.wallet

import co.topl.brambl.Models.{Indices, KeyPair, SigningKey}
import co.topl.brambl.QuivrService
import co.topl.common.Models.{Preimage, VerificationKey}
import co.topl.crypto.signatures
import co.topl.node.box.{Box, Locks}
import co.topl.node.transaction.IoTransaction.Schedule
import co.topl.node.transaction.{Datums, IoTransaction}
import co.topl.node.{Address, Events, Identifiers, KnownIdentifier, KnownIdentifiers}

// Wallet storage api. Will just return dummy values

// TODO: Make a map of Address => KnownId and KnownId => Box. Each address will have a different lock

object MockStorage extends IStorage {

  // Arbitrary Transaction that any new transaction can reference
  private val dummyTx1 = IoTransaction(List(), List(),
    Datums.ioTransactionDatum(Events.IoTransaction(Schedule(0, 5, 100), List(), List(), Array())))
  private val dummyTxIdentifier1 = KnownIdentifiers.TransactionOutput32(0, 0, 0, Identifiers.transaction32(dummyTx1))
  private val dummyTx2 = IoTransaction(List(), List(),
    Datums.ioTransactionDatum(Events.IoTransaction(Schedule(10, 50, 100), List(), List(), Array())))
  private val dummyTxIdentifier2 = KnownIdentifiers.TransactionOutput32(0, 0, 0, Identifiers.transaction32(dummyTx2))

  private val addr1 = Address
  (0, 0, Identifiers.boxLock32(
    Locks.Predicate(List(QuivrService.lockedProposition.get), 1)
  ))


  // Static mappings to provide the Wallet with data
  val idToIdx: Map[KnownIdentifier, Indices] = Map(
    dummyTxIdentifier1 -> Indices(0, 0, 0),
    dummyTxIdentifier2 -> Indices(0, 1, 0)
  )
  val idxToId: Map[Indices, KnownIdentifier] = Map(
    Indices(0, 0, 0) -> dummyTxIdentifier1,
    Indices(0, 1, 0) -> dummyTxIdentifier2
  )

  private def getPredicate(threshold: Int, idx: Indices): Locks.Predicate = Locks.Predicate(
    List(
      QuivrService.lockedProposition.get,
      QuivrService.digestProposition(getPreimage(idx)
        .getOrElse(Preimage("unsolvable preimage".getBytes, "salt".getBytes))
      ).get,
      QuivrService.signatureProposition(getKeyPair(idx)
        .getOrElse(KeyPair(SigningKey("fake sk".getBytes), VerificationKey("fake vk".getBytes)))
        .vk
      ).get,
      QuivrService.heightProposition(2, 8).get,
      QuivrService.tickProposition(2, 8).get,
    ),
    threshold // N of 5 predicate
  )

  private def getSecret(idx: Indices): Array[Byte] = s"${idx.x},${idx.y},${idx.z}".getBytes

  override def getIndicesByIdentifier(id: KnownIdentifier): Option[Indices] = idToIdx.get(id)
  override def getKnownIdentifierByAddress(address: Address): Option[KnownIdentifier] = ???

  override def getBoxByKnownIdentifier(id: KnownIdentifier): Option[Box] = ???

  override def getPreimage(idx: Indices): Option[Preimage] =
    if(idx.x == 0 && idx.y == 0 && idx.z == 0) // Mocking that we only have access to secrets associated with idx 0,0,0
        Some(Preimage(getSecret(idx), "salt".getBytes))
    else None
  override def getKeyPair(idx: Indices): Option[KeyPair] =
    if(idx.x == 0 && idx.y == 0 && idx.z == 0){ // Mocking that we only have access to secrets associated with idx 0,0,0
      val (sk, vk) = signatures.Curve25519.createKeyPair(getSecret(idx))
      Some(KeyPair(SigningKey(sk.value), VerificationKey(vk.value)))
    } else None

}
package co.topl.brambl

import co.topl.brambl.Models.{Indices, KeyPair, SigningKey}
import co.topl.common.Models.{Preimage, VerificationKey}
import co.topl.crypto.signatures
import co.topl.node.transaction.IoTransaction.Schedule
import co.topl.node.transaction.{Datums, IoTransaction}
import co.topl.node.{Events, Identifiers, KnownIdentifier, KnownIdentifiers}

// Wallet storage api. Will just return dummy values

object Wallet {

  // Arbitrary Transaction that any new transaction can reference
  private val dummyTx1 = IoTransaction(List(), List(),
    Datums.ioTransactionDatum(Events.IoTransaction(Schedule(0, 5, 100), List(), List(), Array())))
  private val dummyTxIdentifier1 = KnownIdentifiers.TransactionOutput32(0, 0, 0, Identifiers.transaction32(dummyTx1))
  private val dummyTx2 = IoTransaction(List(), List(),
    Datums.ioTransactionDatum(Events.IoTransaction(Schedule(10, 50, 100), List(), List(), Array())))
  private val dummyTxIdentifier2 = KnownIdentifiers.TransactionOutput32(0, 0, 0, Identifiers.transaction32(dummyTx2))

  // Static mappings to provide the Wallet with data
  val idToIdx: Map[KnownIdentifier, Indices] = Map(
    dummyTxIdentifier1 -> Indices(0, 0, 0),
    dummyTxIdentifier2 -> Indices(0, 1, 0)
  )
  val idxToId: Map[Indices, KnownIdentifier] = Map(
    Indices(0, 0, 0) -> dummyTxIdentifier1,
    Indices(0, 1, 0) -> dummyTxIdentifier2
  )

  def getIndicesByIdentifier(id: KnownIdentifier): Option[Indices] = idToIdx.get(id)
  def getKnownIdentifierByIndices(idx: Indices): Option[KnownIdentifier] = idxToId.get(idx)

  private def getSecret(idx: Indices): Array[Byte] = s"${idx.x},${idx.y},${idx.z}".getBytes
  def getPreimage(idx: Indices): Option[Preimage] =
    if(idx.x == 0 && idx.y == 0 && idx.z == 0) // Mocking that we only have access to secrets associated with idx 0,0,0
        Some(Preimage(getSecret(idx), "salt".getBytes))
    else None
  def getKeyPair(idx: Indices): Option[KeyPair] =
    if(idx.x == 0 && idx.y == 0 && idx.z == 0){ // Mocking that we only have access to secrets associated with idx 0,0,0
      val (sk, vk) = signatures.Curve25519.createKeyPair(getSecret(idx))
      Some(KeyPair(SigningKey(sk.value), VerificationKey(vk.value)))
    } else None
}
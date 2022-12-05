package co.topl.brambl

import co.topl.brambl.Models.Indices
import co.topl.crypto.hash.digest.{Digest32, Digest64}
import co.topl.node.box.Locks
import co.topl.node.typeclasses.ContainsEvidence.SignableOps
import co.topl.node.typeclasses.ContainsSignable.instances.predicateLockSignable
import co.topl.node.{Evidence, Identifiers, KnownIdentifier, KnownIdentifiers}

// Wallet storage api. Will just return dummy values

object Wallet {

  val txEvidence =
    Locks.Predicate(List(), 1) // Should actually be a reference to an existing TX, but for the sake of running the examples
    .blake2bEvidence.sized32Evidence
  val dummyId = KnownIdentifiers.TransactionOutput32(0, 0, 0, Identifiers.IoTransaction32(txEvidence))
  val idToIdx: Map[KnownIdentifier, Indices] = Map(dummyId -> Indices(0, 0, 0))
  val idxToId: Map[Indices, KnownIdentifier] = Map(Indices(0, 0, 0) -> dummyId)
  def getIndicesByIdentifier(id: KnownIdentifier): Indices = idToIdx(id)
  def getKnownIdentifierByIndices(idx: Indices): KnownIdentifier = idxToId(idx)

  // Somehow fetch predicate from KnownIdentifier
  def getPredicateByIdentifier(id: KnownIdentifier): Locks.Predicate = ???

  def getSecret(idx: Indices): Array[Byte] = s"${idx.x},${idx.y},${idx.z}".getBytes
}
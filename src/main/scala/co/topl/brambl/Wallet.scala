package co.topl.brambl

import co.topl.node.box.Locks
import co.topl.node.{KnownIdentifier, KnownReferences}

// Wallet storage api. Will just return dummy values

object Wallet {
  def getIndicesByIdentifier(id: KnownIdentifier): Indices = Indices(0, 0, 0)
  def getSecret(idx: Indices): Array[Byte] = s"${idx.x},${idx.y},${idx.z}".getBytes

  def getPredicate(predicateRef: KnownReferences.Predicate32): Locks.Predicate = ???
}
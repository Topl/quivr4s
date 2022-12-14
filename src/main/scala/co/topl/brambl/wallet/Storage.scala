package co.topl.brambl.wallet

import co.topl.brambl.Models.{Indices, KeyPair}
import co.topl.brambl.models.Address
import co.topl.brambl.models.KnownIdentifier
import co.topl.brambl.models.box.Box
import co.topl.brambl.routines.signatures.Signing
import quivr.models.Preimage

trait Storage {
  // Return the indices associated to a known identifier
  def getIndicesByIdentifier(id: KnownIdentifier): Option[Indices]
  // Return the known identifier associated to an address
  // Simplifying assumption is known identifier and address are 1 to 1
  def getKnownIdentifierByAddress(address: Address): Option[KnownIdentifier]
  // Return the box associated to a known identifier
  // Simplifying assumption is box and known identifier are 1 to 1
  def getBoxByKnownIdentifier(id: KnownIdentifier): Option[Box]
  // Return the preimage associated to indices
  def getPreimage(idx: Indices): Option[Preimage]
  // Return the key pair associated to indices
  def getKeyPair(idx: Indices, routine: Signing): Option[KeyPair]
}

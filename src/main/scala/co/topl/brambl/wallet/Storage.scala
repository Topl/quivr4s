package co.topl.brambl.wallet

import co.topl.brambl.models.Address
import co.topl.brambl.models.KnownIdentifier
import co.topl.brambl.models.box.Box
import co.topl.brambl.routines.signatures.Signing
import quivr.models.{Preimage, KeyPair}
import co.topl.brambl.models.Indices

/**
 * Defines a storage API for storing and managing keys and states.
 */
trait Storage {
  /**
   * Return the indices associated to a known identifier.
   * Simplifying assumption is that KnownIdentifier and Indices are 1 to 1
   *
   * @param id The known identifier for which to retrieve the indices
   * @return The indices associated to the known identifier if it exists. Else None
   */
  def getIndicesByKnownIdentifier(id: KnownIdentifier): Option[Indices]

  /**
   * Return the known identifier associated to the given indices.
   * Simplifying assumption is that KnownIdentifier and Indices are 1 to 1
   *
   * @param idx The indices for which to retrieve the known identifier
   * @return The known identifier associated to the indices if it exists. Else None
   */
  def getKnownIdentifierByIndices(idx: Indices): Option[KnownIdentifier]

  /**
   * Return the known identifier associated to the given address.
   * Simplifying assumption is that KnownIdentifier and Address are 1 to 1
   *
   * @param address The address for which to retrieve the known identifier
   * @return The known identifier associated to the address if it exists. Else None
   */
  def getKnownIdentifierByAddress(address: Address): Option[KnownIdentifier]

  /**
   * Return the box associated to a known identifier.
   * Simplifying assumption is that Box and KnownIdentifier are 1 to 1
   *
   * @param id The known identifier for which to retrieve the box
   * @return The box associated to the known identifier if it exists. Else None
   */
  def getBoxByKnownIdentifier(id: KnownIdentifier): Option[Box]

  /**
   * Return the preimage secret associated to indices.
   *
   * @param idx The indices for which to retrieve the preimage secret
   * @return The preimage secret associated to the indices if it exists. Else None
   */
  def getPreimage(idx: Indices): Option[Preimage]

  /**
   * Return the key pair associated to indices.
   *
   * @param idx The indices for which to retrieve the key pair
   * @param routine The signing routine to use to generate the key pair
   * @return The key pair associated to the indices if it exists. Else None
   */
  def getKeyPair(idx: Indices, routine: Signing): Option[KeyPair]
}

package co.topl.brambl.builders

import cats.Id
import co.topl.brambl.QuivrService
import co.topl.brambl.models.box.Lock
import co.topl.brambl.routines.digests.Blake2b256Digest
import co.topl.brambl.routines.signatures.Curve25519Signature
import co.topl.brambl.wallet.MockStorage.{getKeyPair, getPreimage}
import co.topl.quivr.api.Proposer
import com.google.protobuf.ByteString
import quivr.models.{KeyPair, Preimage, SigningKey, VerificationKey}

/**
 * A mock implementation of an [[LockBuilder]]
 */
object MockLockBuilder extends LockBuilder {
  /**
   * TEMPORARY
   *
   * Construct a mock lock.
   * This is a temporary implementation that hard-codes a Predicate Lock with a single Locked proposition
   * The actual LockBuilder will be designed and implemented in a future effort
   *
   * @return The built 1 of 1 Predicate Lock
   */
  def constructMockLock1of1Locked: Lock =
    Lock().withPredicate(Lock.Predicate(List(Proposer.LockedProposer[Id].propose(None)), 1))

}

package co.topl.brambl.builders

import cats.Id
import co.topl.brambl.models.box.Lock
import co.topl.quivr.api.Proposer

/**
 * A mock implementation of an [[LockBuilder]]
 */
object MockLockBuilder extends LockBuilder {
  /**
   * TEMPORARY
   *
   * Construct a mock lock.
   * This is a temporary implementation that hard codes a Predicate Lock with a single Locked proposition
   * LockBuilder will be designed and implemented in a future effort
   *
   * @return Either an LockBuilderError or the built 1 of 1 Predicate Lock
   */
  def constructMockLockTrivial: Lock =
    Lock().withPredicate(Lock.Predicate(List(Proposer.LockedProposer[Id].propose(None)), 1))
}

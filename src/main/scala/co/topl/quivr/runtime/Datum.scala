package co.topl.quivr.runtime

import co.topl.node.Event

/**
 * Datums represent a queryable product value of the arguments available from a certain Event. Datum may be
 * evaluated during the Quivr protocol execution by providing events as Datum in a Dynamic Context.
 */
abstract class Datum[E <: Event] {
  val event: E
}

/**
 * A special datum indicating that the Event is part of an Event-chain and therefore a height is tracked by participants
 */
abstract class IncludesHeight[E <: Event] extends Datum[E] {
  def height: Long
}

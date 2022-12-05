package co.topl.quivr.runtime

/**
 * Datums represent a queryable product value of the arguments available from a certain Event. Datum may be
 * evaluated during the Quivr protocol execution by providing events as Datum in a Dynamic Context.
 */
trait Datum[+E] {
  val event: E
}

/**
 * A special datum indicating that the Event is part of an Event-chain and therefore a height is tracked by participants
 */
trait IncludesHeight[+E] extends Datum[E] {
  def height: Long
}

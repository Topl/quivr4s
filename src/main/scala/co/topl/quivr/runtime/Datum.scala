package co.topl.quivr.runtime

trait Datum[D] {
  val value: D
}

trait IncludesHeight[T] extends Datum[T] {
  def height: Long
}

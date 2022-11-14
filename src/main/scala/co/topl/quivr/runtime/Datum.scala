package co.topl.quivr.runtime

trait Datum

trait IncludesHeight extends Datum {
  def height: Long
}

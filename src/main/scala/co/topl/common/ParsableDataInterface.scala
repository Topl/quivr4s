package co.topl.common

trait ParsableDataInterface {
  val data: Data
  def parse[E, T](f: Data => Either[E, T]): Either[E, T] = f(data)
}

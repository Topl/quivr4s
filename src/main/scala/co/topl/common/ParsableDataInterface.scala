package co.topl.common

trait ParsableDataInterface[F[_]] {
  val data: Data
  def parse[E, T](f: Data => F[Either[E, T]]): F[Either[E, T]] = f(data)
}

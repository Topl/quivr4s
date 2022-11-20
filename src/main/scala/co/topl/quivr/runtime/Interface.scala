package co.topl.quivr.runtime

import co.topl.common

trait Interface[F[_]] {
  val data: common.Data
  def parse[E, T](f: common.Data => Either[E, T]): Either[E, T] = f(data)
}

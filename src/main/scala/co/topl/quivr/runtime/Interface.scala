package co.topl.quivr.runtime

import co.topl.quivr.User

trait Interface {
  val data: User.Data
  def parse[T](f: User.Data => Option[T]): Option[T] = f(data)
}

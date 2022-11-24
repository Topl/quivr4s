package co.topl.node

import co.topl.quivr

case class Predicate(conditions: List[quivr.Proposition], threshold: Int)

object Predicate {
  case class Id(bytes: Array[Byte])

  case class Known(conditions: List[Option[quivr.Proposition]])

  // use a Root here so we can provide a membership proof of the conditions
  case class Image(root: Root, threshold: Int)

  // use a merkle root to commit to the list of conditions
  def idFromImage(image: Predicate.Image): Predicate.Id = ???
}

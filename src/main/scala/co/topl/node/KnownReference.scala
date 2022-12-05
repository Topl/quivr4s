package co.topl.node

sealed abstract class KnownReference[O] {
  val network: Int
  val ledger: Int
  val indices: List[Int]
  val reference: Reference[O]
}

object KnownReferences {

  case class Predicate(
    network:   Int,
    ledger:    Int,
    indices:   List[Int],
    reference: Reference[Events.SpentTransactionOutput]
  ) extends KnownReference[Events.SpentTransactionOutput]

  case class Blob(
    network:   Int,
    ledger:    Int,
    indices:   List[Int],
    reference: Reference[Events.UnspentTransactionOutput]
  ) extends KnownReference[Events.UnspentTransactionOutput]

  case class Leaf(network: Int, ledger: Int, indices: List[Int], reference: Reference[Events.Root])
      extends KnownReference[Events.Root]

}

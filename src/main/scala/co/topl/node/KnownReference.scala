package co.topl.node

trait KnownReference[O] {
  val network: Int
  val ledger: Int
  val indices: List[Int]
  val reference: Reference[O]
}

object KnownReferences {

  case class Predicate32(
    network:   Int,
    ledger:    Int,
    indices:   List[Int],
    reference: Reference[Events.SpentTransactionOutput]
  ) extends KnownReference[Events.SpentTransactionOutput]

  case class Predicate64(
    network:   Int,
    ledger:    Int,
    indices:   List[Int],
    reference: Reference[Events.SpentTransactionOutput]
  ) extends KnownReference[Events.SpentTransactionOutput]

  case class Blob32(
    network:   Int,
    ledger:    Int,
    indices:   List[Int],
    reference: Reference[Events.UnspentTransactionOutput]
  ) extends KnownReference[Events.UnspentTransactionOutput]

  case class Blob64(
    network:   Int,
    ledger:    Int,
    indices:   List[Int],
    reference: Reference[Events.UnspentTransactionOutput]
  ) extends KnownReference[Events.UnspentTransactionOutput]

  case class Leaf32(network: Int, ledger: Int, indices: List[Int], reference: Reference[Events.Root])
      extends KnownReference[Events.Root]

  case class Leaf64(network: Int, ledger: Int, indices: List[Int], reference: Reference[Events.Root])
      extends KnownReference[Events.Root]

}

package co.topl.node

import co.topl.node.typeclasses.Identifiers

case class Address(network: Int, ledger: Int, evidence: Identifiers.Predicate)

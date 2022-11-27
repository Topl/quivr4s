package co.topl.node.transaction

import co.topl.node.Reference

case class Address(network: Int, ledger: Int, reference: Reference)

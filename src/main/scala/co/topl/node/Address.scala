package co.topl.node

/**
 * An address is a location specific (network + ledger) path to a certain Identitier
 * @param network the chain an Address will settle to (targets the security behind an Addresses usage)
 * @param ledger the application an Address interacts with directly
 * @param identifier the tagged, unique value targeted by a given Address
 */
case class Address(network: Int, ledger: Int, identifier: Identifier)

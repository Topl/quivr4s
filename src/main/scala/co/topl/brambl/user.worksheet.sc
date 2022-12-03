import co.topl.brambl.TransactionBuilder
import co.topl.brambl.Credentials
import co.topl.brambl.QuivrService
import co.topl.node.KnownIdentifiers

// Worksheet to use components to illustrate the examples
// To simplify, for functions/fields with both a 32 and 64 version, I am only considering the 32 version


// Example 1: Fetch Txo references from Indices (Optional)
// TODO

// Example 2: Create unproven Tx from Txo references
// If the user already has the Txos, then Example 1 can be skipped.
val utxoRef = KnownIdentifiers.TransactionOutput32(0, 0,
  index = 0, // Referencing the 1st output in the Transaction (given by id)
  id = ??? // Referring to a transaction that already exists
)
val unprovenTx = TransactionBuilder.constructIoTransaction(List(utxoRef))

// Example 3: Prove unproven Tx via Credentials
val tx = Credentials prove unprovenTx

// Example 4: Validate the Tx
// This is a call to ValidationInterpreter.make.validate
printResult(QuivrService validate tx)

def printResult(valid: Boolean): Unit = {
  val prefix = if(valid) "" else "in"
  print(s"tx is ${prefix} valid")
}


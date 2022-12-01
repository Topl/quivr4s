import co.topl.brambl.TransactionBuilder
import co.topl.brambl.Credentials
import co.topl.brambl.QuivrService
import co.topl.node.References

// Worksheet to use components to illustrate the examples


// Example 1: Fetch Txo references from Indices (Optional)
// TODO

// Example 2: Create unproven Tx from Txo references
// If the user already has the Txos, then Example 1 can be skipped.
val utxoRef = References.KnownSpendable32(0, 0,
  indices = List(0), // Referencing the 1st output in the Transaction (given by id)
  id = ??? // Referring to a transaction that already exists
)
val unprovenTx = TransactionBuilder.constructIoTransaction(List(utxoRef))

// Example 3: Prove unproven Tx via Credentials
val tx = Credentials prove unprovenTx

// Example 4: Validate the Tx
// This is a call to ValidationInterpreter.make.validate
QuivrService validate tx

def printResult(valid: Boolean): Unit = {
  val prefix = if(valid) "" else "in"
  print(s"tx is ${prefix} valid")
}


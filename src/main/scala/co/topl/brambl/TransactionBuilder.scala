package co.topl.brambl

import co.topl.node.transaction.IoTransaction
import co.topl.node.transaction.Outputs.{Spent, Unspent}


// Create un-proven transactions

object TransactionBuilder {

  private def constructSpentOutput: Spent = ???

  private def constructUnspentOutput: Unspent = ???

  // Takes in a list of Txos and more
  def constructIoTransaction: IoTransaction  = ???
}


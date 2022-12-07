///**
//* A worksheet for using the Credentialler
//* How the IoTransaction is created is irrelevant
//* */
//import cats.implicits.toShow
//import co.topl.brambl.{Credentials, QuivrService, TransactionBuilder, Wallet}
//import co.topl.brambl.Models._
//import co.topl.common.Models.Digest
//import co.topl.node.{Address, Events, Identifiers}
//import co.topl.node.box.{Locks, Values}
//import co.topl.node.transaction.{Attestations, Datums, IoTransaction, SpentTransactionOutput, UnspentTransactionOutput}
//import co.topl.quivr.runtime.Datum
//import co.topl.crypto.hash.blake2b256
//
//
//object Test {
//
//  // make the 2 contexts here. Make ti implicit and call getContext(tx)
//  def signableBytesAreNotMutated(unprovenTx: IoTransaction): Unit = {
//
//  }
//
//  // helper to print a transaction
//  def printTxResult(tx: IoTransaction): Unit = {
//    val isValidPrefix = if (QuivrService.validate(tx)) "" else "not "
//    println(s"====")
//    println(s"The following transaction is ${isValidPrefix}authorized")
//    println(tx.show)
//  }
//}
//
//
//
//
//// The only difference between these 2 transaction is the output.
//// Each have 1 output with a 2 proposition predicate (digest and locked).
//// The first is 1 of 1 (thus provable)
//// The second is 2 of 2 (thus unprovable)
//val iotx1of2Unproven = TransactionBuilder.constructTestTransaction(1)
//val iotx2of2Unproven = TransactionBuilder.constructTestTransaction(2)
//
//// Proving transaction should be as simple as taking the unproven transaction
//val iotx1of2Proven = Credentials.prove(iotx1of2Unproven)
//val iotx2of2Proven = Credentials.prove(iotx2of2Unproven)
//
//printTxResult(iotx1of2Unproven)
//printTxResult(iotx1of2Proven)
//printTxResult(iotx2of2Unproven)
//printTxResult(iotx2of2Proven)
package co.topl.genus

import co.topl.node.Address
import co.topl.node.transaction.Box

object Models {
  trait TxoState
  object TxoStates {
    object UNSPENT extends TxoState
  }

  case class Txo(id: Box.Id, box: Box, address: Address, state: TxoState = TxoStates.UNSPENT)
}

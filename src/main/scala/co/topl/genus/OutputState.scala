package co.topl.genus

sealed abstract class OutputState

object OutputStates {
  case object Spent extends OutputState
  case object Unspent extends OutputState
}

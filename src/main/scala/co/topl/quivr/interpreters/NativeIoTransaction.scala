package co.topl.quivr.interpreters

import co.topl.quivr.algebras.ValidateAlgebra
import co.topl.quivr.{Box, IoTransaction, Metadata}

trait NativeIoTransaction[V <: Box.Value] extends IoTransaction[STxO[_], UTxO[_]] {
  val inputs: List[STxO[V]]
  val outputs: List[UTxO[V]]
}

case class NativeTransaction[V <: Box.Value](inputs: List[STxO[V]],
                                outputs: List[UTxO[V]],
                                datum: Option[NativeTransaction.Datum]
                               ) extends NativeIoTransaction[V]

object NativeTransaction {
  case class Schedule(minSlot: Long, maxSlot: Long, timestamp: Long)

  case class Datum(schedule: Schedule, data: Option[Metadata])

  implicit def transactionValidate[V]: ValidateAlgebra[NativeIoTransaction[_]] = ???
  // check all inputs are authorized to be spent
  // check that all outputs are valid transformations

//    (value: NativeIoTransaction[V]) => value.inputs.

}

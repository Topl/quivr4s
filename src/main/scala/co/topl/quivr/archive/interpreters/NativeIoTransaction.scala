package co.topl.quivr.archive.interpreters

import co.topl.quivr.archive.{Box, IoTransaction, Metadata}

trait NativeIoTransaction extends IoTransaction[STxO, UTxO] {
  val inputs: List[STxO[_]]
  val outputs: List[UTxO[_]]
}

case class NativeTransaction[V <: Box.Value](
    inputs: List[STxO[V]],
    outputs: List[UTxO[V]],
    datum: Option[NativeTransaction.Datum]
) extends NativeIoTransaction

object NativeTransaction {
  case class Schedule(minSlot: Long, maxSlot: Long, timestamp: Long)

  case class Datum(schedule: Schedule, data: Option[Metadata])

  // implicit def transactionValidate[V]: ValidateAlgebra[NativeIoTransaction[_]] = ???
  // check all inputs are authorized to be spent
  // check that all outputs are valid transformations

//    (value: NativeIoTransaction[V]) => value.inputs.

}

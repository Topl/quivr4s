package co.topl.brambl.builders

import co.topl.brambl.models.Indices
import co.topl.brambl.models.Datum.{SpentOutput => Datum}
import co.topl.brambl.models.transaction.SpentTransactionOutput


object MockInputBuilder extends InputBuilder {
  override def constructUnprovenInput(idx: Indices, datum: Option[Datum]): Either[BuilderError, SpentTransactionOutput] = ???
}

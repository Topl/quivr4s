package co.topl.brambl.builders

import co.topl.brambl.models.Indices
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.models.Datum.{SpentOutput => Datum}

trait InputBuilder {
  def constructUnprovenInput(idx: Indices, datum: Option[Datum]): Either[BuilderError, IoTransaction]
}

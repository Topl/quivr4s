package co.topl.brambl.builders

import co.topl.brambl.builders.Models.InputBuildRequest
import co.topl.brambl.models.transaction.SpentTransactionOutput


object MockInputBuilder extends InputBuilder {
  override def constructUnprovenInput(data: InputBuildRequest): Either[BuilderError, SpentTransactionOutput] = ???
}

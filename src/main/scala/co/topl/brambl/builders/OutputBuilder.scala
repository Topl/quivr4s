package co.topl.brambl.builders

import co.topl.brambl.builders.Models.OutputBuildRequest
import co.topl.brambl.models.transaction.UnspentTransactionOutput

trait OutputBuilder {
  def constructOutput(data: OutputBuildRequest): Either[BuilderError, UnspentTransactionOutput]
}
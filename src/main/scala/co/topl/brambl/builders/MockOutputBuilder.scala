package co.topl.brambl.builders

import co.topl.brambl.builders.Models.OutputBuildRequest
import co.topl.brambl.models.transaction.UnspentTransactionOutput

object MockOutputBuilder extends OutputBuilder {
  override def constructOutput(data: OutputBuildRequest): Either[BuilderError, UnspentTransactionOutput] = ???
}

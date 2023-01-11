package co.topl.brambl.builders

import co.topl.brambl.models.Indices
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.brambl.models.Datum.{UnspentOutput => Datum}
import co.topl.brambl.models.box.{Lock, Value}

trait OutputBuilder {
  def constructOutput(
                       idx: Indices,
                       datum: Option[Datum],
                       lock: Lock,
                       value: Value
                     ): Either[BuilderError, IoTransaction]
}

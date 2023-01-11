package co.topl.brambl.builders

import co.topl.brambl.models.Indices
import co.topl.brambl.models.box.{Lock, Value}
import co.topl.brambl.models.transaction.UnspentTransactionOutput
import co.topl.brambl.models.Datum.{UnspentOutput => Datum}

object MockOutputBuilder extends OutputBuilder {
  override def constructOutput(
                                idx: Indices,
                                datum: Option[Datum],
                                lock: Lock,
                                value: Value
                              ): Either[BuilderError, UnspentTransactionOutput] = ???
}

package co.topl.brambl.builders

import co.topl.brambl.models.{Datum, KnownIdentifier}
import co.topl.brambl.models.box.{Lock, Value}

// Temporary until they are added to PB
object Models {
  case class OutputBuildRequest(datum: Option[Datum.UnspentOutput], lock: Lock, value: Value)

  case class InputBuildRequest(id: KnownIdentifier, datum: Option[Datum.SpentOutput])
}

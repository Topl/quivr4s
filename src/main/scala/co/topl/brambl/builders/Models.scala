package co.topl.brambl.builders

import co.topl.brambl.models.Indices
import co.topl.brambl.models.box.{Lock, Value}
import co.topl.brambl.models.Datum

// Temporary until they are added to PB
object Models {
  case class OutputBuildRequest(
    datum: Option[Datum.UnspentOutput],
    lock: Lock,
    value: Value
  )

  case class InputBuildRequest(
    idx: Indices,
    datum: Option[Datum.SpentOutput]
  )
}

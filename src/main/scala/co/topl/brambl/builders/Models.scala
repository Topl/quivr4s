package co.topl.brambl.builders

import co.topl.brambl.models.Indices
import co.topl.brambl.models.box.{Lock, Value}
import co.topl.brambl.models.Datum.{
  SpentOutput => SpentOutputDatum,
  UnspentOutput => UnspentOutputDatum
}

// Temporary until they are added to PB
object Models {
  case class OutputBuildRequest(
    datum: Option[UnspentOutputDatum],
    lock: Lock,
    value: Value
  )

  case class InputBuildRequest(
    idx: Indices,
    datum: Option[SpentOutputDatum]
  )
}

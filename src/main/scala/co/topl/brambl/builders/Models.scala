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
    idx: Indices, // The output indices need to end up as a list of addresses
                  // Addresses contain the identifier
                  // The identifier will need to encode evidence of the lock
    datum: Option[UnspentOutputDatum],
    lock: Lock,
    value: Value
  )

  case class InputBuildRequest(
    idx: Indices,
    datum: Option[SpentOutputDatum]
  )
}

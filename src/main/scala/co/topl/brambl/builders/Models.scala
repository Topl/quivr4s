package co.topl.brambl.builders

import co.topl.brambl.models.Indices
import co.topl.brambl.models.box.{Lock, Value}
import co.topl.brambl.models.Datum.{
  SpentOutput => SpentOutputDatum,
  UnspentOutput => UnspentOutputDatum
}

// Temporary until they are added to PB
object Models {
  trait OutputBuildRequest {
    def idx: Indices // The output indices need to end up as a list of addresses
                     // Addresses contain the identifier
                     // The identifier will need to encode evidence of the lock
    def datum: Option[UnspentOutputDatum]
    def lock: Lock
    def value: Value
  }

  trait InputBuildRequest {
    def idx: Indices
    def datum: Option[SpentOutputDatum]
  }
}

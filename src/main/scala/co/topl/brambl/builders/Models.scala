package co.topl.brambl.builders

import co.topl.brambl.models.KnownIdentifier
import co.topl.brambl.models.box.{Lock, Value}
import quivr.models.SmallData

// Temporary until they are added to PB
object Models {
  case class OutputBuildRequest(lock: Lock, value: Value, metadata: Option[SmallData])

  case class InputBuildRequest(id: KnownIdentifier, metadata: Option[SmallData])
}

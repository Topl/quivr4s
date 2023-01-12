package co.topl.brambl.builders

trait BuilderError

object BuilderErrors {
  case class InputBuilderError(message: String) extends BuilderError
}
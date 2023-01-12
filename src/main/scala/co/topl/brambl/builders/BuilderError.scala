package co.topl.brambl.builders

/**
 * A generic error type that is returned by the builders when
 * a build is unsuccessful.
 *
 * @param message The error message
 */
abstract class BuilderError(val message: String)

object BuilderErrors {
  /**
   * A Builder error indicating that a IoTransaction's input (SpentTransactionOutput)
   * was unable to be successfully built.
   *
   * @param message The error message indicating why the build is unsuccessful
   */
  case class InputBuilderError(override val message: String) extends BuilderError(message)

  /**
   * A Builder error indicating that a IoTransaction's output (UnspentTransactionOutput)
   * was unable to be successfully built.
   *
   * @param message The error message indicating why the build is unsuccessful
   */
  case class OutputBuilderError(override val message: String) extends BuilderError(message)
}
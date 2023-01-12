package co.topl.brambl.builders

import cats.implicits.catsSyntaxOptionId
import co.topl.brambl.builders.BuilderErrors.OutputBuilderError
import co.topl.brambl.builders.Models.OutputBuildRequest
import co.topl.brambl.models.transaction.UnspentTransactionOutput
import co.topl.brambl.models.{Address, Identifier}
import co.topl.brambl.typeclasses.ContainsEvidence._
import co.topl.brambl.typeclasses.ContainsSignable.instances.lockSignable

/**
 * A mock implementation of an OutputBuilder
 */
object MockOutputBuilder extends OutputBuilder {

  val NETWORK = 0
  val LEDGER = 0

  /**
   * Construct a IoTransaction output (UnspentTransactionOutput).
   *
   * @param data The data required to build an UnspentTransactionOutput
   *             The data is an object with the following fields:
   *             datum: Option[Datum.UnspentOutput] - Additional data to include in the built UnspentTransactionOutput
   *             lock: Lock - The lock for the built UnspentTransactionOutput. It will be encoded in the address
   *             value: Value - The value for the built UnspentTransactionOutput
   * @return Either a OutputBuilderError or the built UnspentTransactionOutput
   */
  override def constructOutput(data: OutputBuildRequest): Either[OutputBuilderError, UnspentTransactionOutput] = {
    val address = Address(NETWORK, LEDGER,
      Identifier().withLock32(Identifier.Lock32(data.lock.sized32Evidence.some)).some
    )
    val value = data.value
    val datum = data.datum
    Right(UnspentTransactionOutput(address.some, value.some, datum))
  }
}

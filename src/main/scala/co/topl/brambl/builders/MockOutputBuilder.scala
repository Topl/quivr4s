package co.topl.brambl.builders

import cats.implicits.catsSyntaxOptionId
import co.topl.brambl.builders.Models.OutputBuildRequest
import co.topl.brambl.models.transaction.UnspentTransactionOutput
import co.topl.brambl.models.{Address, Identifier}
import co.topl.brambl.typeclasses.ContainsEvidence._
import co.topl.brambl.typeclasses.ContainsSignable.instances.lockSignable

object MockOutputBuilder extends OutputBuilder {

  val NETWORK = 0
  val LEDGER = 0
  override def constructOutput(data: OutputBuildRequest): Either[BuilderError, UnspentTransactionOutput] = {
    val address = Address(NETWORK, LEDGER,
      Identifier().withLock32(Identifier.Lock32(data.lock.sized32Evidence.some)).some
    )
    val value = data.value
    val datum = data.datum
    Right(UnspentTransactionOutput(address.some, value.some, datum))
  }
}

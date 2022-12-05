package co.topl.node.transaction

import co.topl.node.Events
import co.topl.quivr.runtime.{Datum, IncludesHeight}

object Datums {

  def eonDatum(datum: Events.Eon): IncludesHeight[Events.Eon] =
    new IncludesHeight[Events.Eon] {
      override def height: Long = datum.height

      override val event: Events.Eon = datum
    }

  def eraDatum(datum: Events.Era): IncludesHeight[Events.Era] =
    new IncludesHeight[Events.Era] {
      override def height: Long = datum.height

      override val event: Events.Era = datum
    }

  def epochDatum(datum: Events.Epoch): IncludesHeight[Events.Epoch] =
    new IncludesHeight[Events.Epoch] {
      override def height: Long = datum.height

      override val event: Events.Epoch = datum
    }

  def headerDatum(datum: Events.Header): IncludesHeight[Events.Header] =
    new IncludesHeight[Events.Header] {
      override def height: Long = datum.height

      override val event: Events.Header = datum
    }

  def rootDatum(datum: Events.Root): Datum[Events.Root] =
    new Datum[Events.Root] {
      override val event: Events.Root = datum
    }

  def ioTransactionDatum(datum: Events.IoTransaction): Datum[Events.IoTransaction] =
    new Datum[Events.IoTransaction] {
      override val event: Events.IoTransaction = datum
    }

  def spentOutputDatum(datum: Events.SpentTransactionOutput): Datum[Events.SpentTransactionOutput] =
    new Datum[Events.SpentTransactionOutput] {
      override val event: Events.SpentTransactionOutput = datum
    }

  def unspentOutputDatum(datum: Events.UnspentTransactionOutput): Datum[Events.UnspentTransactionOutput] =
    new Datum[Events.UnspentTransactionOutput] {
      override val event: Events.UnspentTransactionOutput = datum
    }

}

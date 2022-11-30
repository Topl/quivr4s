package co.topl.node.transaction

import co.topl.node.{Events, References, Root, SmallData}
import co.topl.quivr.runtime.{Datum, IncludesHeight}



object Datums {

  def eonDatum(datum: Events.Eon): IncludesHeight[Events.Eon] =
    new IncludesHeight[Events.Eon] {
      override def height: Long = datum.height

      override val value: Events.Eon = datum
    }

  def eraDatum(datum: Events.Era): IncludesHeight[Events.Era] =
    new IncludesHeight[Events.Era] {
      override def height: Long = datum.height

      override val value: Events.Era = datum
    }

  def epochDatum(datum: Events.Epoch): IncludesHeight[Events.Epoch] =
    new IncludesHeight[Events.Epoch] {
      override def height: Long = datum.height

      override val value: Events.Epoch = datum
    }

  def headerDatum(datum: Events.Header): IncludesHeight[Events.Header] =
    new IncludesHeight[Events.Header] {
      override def height: Long = datum.height

      override val value: Events.Header = datum
    }

  def bodyDatum(datum: Events.Header): Datum[Events.Header] =
    new Datum[Events.Header] {
      override val value: Events.Header = datum
    }

  def ioTransactionDatum(datum: Events.Header): Datum[Events.Header] =
    new Datum[Events.Header] {
      override val value: Events.Header = datum
    }

  def spentOutputDatum(datum: Events.SpentOutput): Datum[Events.SpentOutput] =
    new Datum[Events.SpentOutput] {
      override val value: Events.SpentOutput = datum
    }

  def unspentOutputDatum(datum: Events.UnspentOutput): Datum[Events.UnspentOutput] =
    new Datum[Events.UnspentOutput] {
      override val value: Events.UnspentOutput = datum
    }

}

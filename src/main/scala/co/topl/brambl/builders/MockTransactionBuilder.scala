package co.topl.brambl.builders

import cats.implicits.catsSyntaxOptionId
import co.topl.brambl.models.KnownIdentifier.{TransactionOutput32, TransactionOutput64}
import co.topl.brambl.models.builders.{InputBuildRequest, OutputBuildRequest}
import co.topl.brambl.models.{Datum, Event}
import co.topl.brambl.models.transaction.{IoTransaction, SpentTransactionOutput, UnspentTransactionOutput}
import co.topl.brambl.models.transaction.Schedule
import quivr.models.SmallData
import com.google.protobuf.ByteString

/**
 * A mock implementation of an [[TransactionBuilder]]
 */
object MockTransactionBuilder extends TransactionBuilder {

  private final val EmptyData = SmallData(ByteString.EMPTY)

  override def constructUnprovenTransaction(
                                             inputRequests: List[InputBuildRequest],
                                             outputRequests: List[OutputBuildRequest],
                                             schedule: Option[Schedule] = None,
                                             output32Refs: List[TransactionOutput32] = List(),
                                             output64Refs: List[TransactionOutput64] = List(),
                                             metadata: Option[SmallData] = None
                                           ): Either[List[BuilderError], IoTransaction] = {
    val inputs = inputRequests
      .map(MockInputBuilder.constructUnprovenInput)
      .partitionMap[BuilderError, SpentTransactionOutput](identity)
    val outputs = outputRequests
      .map(MockOutputBuilder.constructOutput)
      .partitionMap[BuilderError, UnspentTransactionOutput](identity)
    if(inputs._1.isEmpty && outputs._1.isEmpty) {
      val datum = Datum.IoTransaction(Event.IoTransaction(
        if(schedule.isDefined) schedule
        else Schedule(0, 2147483647, System.currentTimeMillis).some, // TODO: Replace min and max with slot numbers
        output32Refs,
        output64Refs,
        if(metadata.isDefined) metadata else EmptyData.some
      ).some)
      Right(IoTransaction(inputs._2, outputs._2, datum.some))
    } else
      Left(inputs._1 ++ outputs._1)
  }
}

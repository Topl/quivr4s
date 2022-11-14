object EUtxOPaper {

    type Tick = Long

    type Value = Int

    type Digest = Array[Byte]
    type Address = (Int, Int, Array[Byte])

    type TxId = Array[Byte]
    type Ref = (TxId, Int)

    type Script = Array[Byte] //arb
    type Redeemer = Array[Byte] //arb
    type Data = Array[Byte] //arb


  case class PTx(
    inputs:   Set[PTx.SpentOutput],
    outputs:  List[PTx.UnspentOutput],
    validityInterval: PTx.TickInterval,
  )

  object PTx {
    case class TickInterval(min: Long, max: Long)

    case class SpentOutput(outputRef: Ref, validatorScript: Script, redeemer: Redeemer, datum: Data)

    case class UnspentOutput(address: Address, datumHash: Digest, value: Value)
  }


}

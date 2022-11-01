package co.topl.quivr.algebras

import cats.data.Reader
import scorex.crypto.signatures.PublicKey

case class ProposerContext(vks: List[PublicKey])

case class SignatureProposition(idx: Int, vk: PublicKey)

object Proposer {

  def createProposition(
      quivrContract: QuivrContractExpr[IndexedSignature]
  )(implicit ctx: ProposerContext): QuivrContractExpr[SignatureProposition] =
    quivrContract.map(x => SignatureProposition(x.idx, ctx.vks(x.idx - 1)))

}

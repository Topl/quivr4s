package co.topl.quivr.algebras

import cats.data.Reader
import scorex.crypto.signatures.PublicKey
import scorex.crypto.signatures.PrivateKey
import co.topl.quivr.interpreters.NativeTransaction
import co.topl.quivr.Box
import scorex.crypto.signatures.Curve25519
import scorex.crypto.signatures.Signature

case class ProverContext(
    vks: List[PublicKey],
    sks: List[PrivateKey],
    nativeTransaction: NativeTransaction[Box.Value]
)

case class SignatureProof(signature: Signature)

object Prover {

  def prove(
      quivrContract: QuivrContractExpr[IndexedSignature]
  )(implicit ctx: ProverContext): QuivrContractExpr[SignatureProof] = {
    quivrContract
      .map(x => SignatureProposition(x.idx, ctx.vks(x.idx - 1)))
      .map { vk =>
        SignatureProof(
          Curve25519
            .sign(ctx.sks(vk.idx - 1), ctx.nativeTransaction.signableBytes)
        )
      }
  }

}

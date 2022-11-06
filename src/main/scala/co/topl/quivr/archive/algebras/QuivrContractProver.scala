package co.topl.quivr.archive.algebras

import co.topl.quivr.archive.Box
import co.topl.quivr.archive.interpreters.NativeTransaction
import scorex.crypto.signatures.{Curve25519, PrivateKey, PublicKey, Signature}

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

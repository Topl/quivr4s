package co.topl.quivr.archive.algebras

import co.topl.quivr.archive.Box
import co.topl.quivr.archive.interpreters.NativeTransaction
import scorex.crypto.signatures.{Curve25519, PublicKey}

case class VerifierContext(
    vks: List[PublicKey],
    currentHeight: Long,
    nativeTransaction: NativeTransaction[Box.Value]
)

object Verifier {

  def verify(
      pProposition: QuivrContractExpr[SignatureProposition],
      pProof: QuivrContractExpr[SignatureProof]
  )(implicit ctx: VerifierContext): QuivrContractExpr[Boolean] = {
    pProposition.zip(pProof).map { e =>
      val (proposition, proof) = e
      Curve25519
        .verify(
          proof.signature,
          ctx.nativeTransaction.signableBytes,
          proposition.vk
        )
    }
  }

  def eval(
      verificationResult: QuivrContractExpr[Boolean]
  )(implicit ctx: VerifierContext): Boolean =
    verificationResult match {
      case False            => false
      case Not(e)           => !eval(e)
      case And(left, right) => eval(left) && eval(right)
      case Or(left, right)  => eval(left) || eval(right)
      case Threshold(threshold, props) =>
        props.map(eval).count(identity) >= threshold
      case HeightLock(height) => ctx.currentHeight >= height
      case Signature(e)       => e
    }

}

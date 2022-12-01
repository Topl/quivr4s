package co.topl.brambl

import co.topl.quivr.Models.Primitive.Digest
import co.topl.quivr.runtime.DynamicContext
import co.topl.node.transaction.authorization.ValidationInterpreter
import co.topl.quivr.api.Verifier
import co.topl.node.transaction.IoTransaction
import co.topl.quivr.SignableBytes

// Easy to use Topl-opinionated layer for Brambl to use to access the un-opinionated quivr API

object QuivrService {
  type ExecutionCtx[T] = T // Trivial runtime execution context
  val context: DynamicContext[ExecutionCtx, String] = ???

  def digestProposition: Digest.Proposition = ???
  def digestProof(msg: SignableBytes): Digest.Proof = ???

  def validate(tx: IoTransaction): Boolean = {
    implicit val verifier: Verifier[ExecutionCtx] = Verifier.instances.verifierInstance
    ValidationInterpreter
      .make[ExecutionCtx]()
      .validate(context)(tx)
      .isRight
  }
}

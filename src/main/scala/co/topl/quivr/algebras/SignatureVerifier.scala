package co.topl.quivr.algebras

import co.topl.common.SignatureVerification
import co.topl.quivr.runtime.QuivrRuntimeError

trait SignatureVerifier[F[_]] extends ContextlessValidation[F, QuivrRuntimeError, SignatureVerification]

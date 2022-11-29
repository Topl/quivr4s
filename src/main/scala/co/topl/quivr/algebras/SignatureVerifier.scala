package co.topl.quivr.algebras

import co.topl.common.ContextlessValidation
import co.topl.common.Models.SignatureVerification
import co.topl.quivr.runtime.QuivrRuntimeError

trait SignatureVerifier[F[_]] extends ContextlessValidation[F, QuivrRuntimeError, SignatureVerification]

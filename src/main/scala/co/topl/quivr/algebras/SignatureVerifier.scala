package co.topl.quivr.algebras

import co.topl.common.SignatureVerification
import co.topl.quivr.runtime.QuivrError

trait SignatureVerifier[F[_]] extends ContextlessValidation[F, QuivrError, SignatureVerification]

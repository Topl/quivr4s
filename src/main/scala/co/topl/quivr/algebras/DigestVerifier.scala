package co.topl.quivr.algebras

import co.topl.common.DigestVerification
import co.topl.quivr.runtime.QuivrRuntimeError

trait DigestVerifier[F[_]] extends ContextlessValidation[F, QuivrRuntimeError, DigestVerification]

package co.topl.quivr.algebras

import co.topl.common.DigestVerification
import co.topl.quivr.runtime.QuivrError

trait DigestVerifier[F[_]] extends ContextlessValidation[F, QuivrError, DigestVerification]

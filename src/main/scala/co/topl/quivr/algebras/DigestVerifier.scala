package co.topl.quivr.algebras

import co.topl.quivr.{runtime}
import co.topl.common.DigestVerification

trait DigestVerifier[F[_]] extends ContextlessValidation[F, runtime.Error, DigestVerification]

package co.topl.quivr.algebras

import co.topl.quivr.{runtime}
import co.topl.common.SignatureVerification

trait SignatureVerifier[F[_]] extends ContextlessValidation[F, runtime.Error, SignatureVerification]

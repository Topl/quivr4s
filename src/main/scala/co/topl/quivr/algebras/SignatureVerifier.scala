package co.topl.quivr.algebras

import co.topl.common.ContextlessValidation
import co.topl.common.Models.SignatureVerification
import co.topl.quivr.runtime.QuivrRuntimeError

/** A trait that provides Signature verification for use in a Dynamic Context */
trait SignatureVerifier[F[_]] extends ContextlessValidation[F, QuivrRuntimeError, SignatureVerification]

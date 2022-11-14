package co.topl.quivr.algebras

import co.topl.quivr.{SignableTxBytes, User, runtime}

trait SignatureVerifier[F[_]] extends ContextlessValidation[F, runtime.Error, (User.VerificationKey, User.Witness, SignableTxBytes)]

package co.topl.quivr.algebras

import co.topl.quivr.{User, runtime}

trait DigestVerifier[F[_]] extends ContextlessValidation[F, runtime.Error, (User.Preimage, User.Digest)]

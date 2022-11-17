package co.topl.quivr.algebras

import co.topl.quivr.{runtime}

case class Digest

trait DigestVerifier[F[_]] extends ContextlessValidation[F, runtime.Error, (User.Preimage, User.Digest)]

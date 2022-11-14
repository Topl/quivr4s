package co.topl.quivr.runtime

import cats.Monad
import cats.data.EitherT
import co.topl.quivr
import co.topl.quivr.algebras.{DigestVerifier, SignatureVerifier}
import co.topl.quivr.runtime.errors.ContextErrors
import co.topl.quivr.{SignableTxBytes, User}

trait DynamicContext[F[_], K] {
  val datums: Map[K, Datum]
  val interfaces: Map[K, Interface]

  val signingRoutines: Map[K, SignatureVerifier[F]]
  val hashingRoutines: Map[K, DigestVerifier[F]]

  def signableBytes: F[SignableTxBytes]

  def currentTick: F[Long]

  def heightOf(label: K)(implicit monad: Monad[F]): EitherT[F, quivr.runtime.Error, Long] =
    EitherT.fromOption(datums.get(label), ContextErrors.FailedToFindDatum: Error).map {
      case v: IncludesHeight => v.height
    }

  def digestVerify(routine: K)(preimage: User.Preimage, digest: User.Digest)(implicit
    monad:                  Monad[F]
  ): EitherT[F, quivr.runtime.Error, (User.Preimage, User.Digest)] = for {
    verifier <- EitherT.fromOption(hashingRoutines.get(routine), ContextErrors.FailedToFindDigestVerifier)
    res      <- EitherT(verifier.validate(preimage, digest))
  } yield res

  def signatureVerify(routine: K)(vk: User.VerificationKey, sig: User.Witness, msg: SignableTxBytes)(implicit
    monad:                     Monad[F]
  ): EitherT[F, quivr.runtime.Error, (User.VerificationKey, User.Witness, SignableTxBytes)] = for {
    verifier <- EitherT.fromOption(signingRoutines.get(routine), ContextErrors.FailedToFindSignatureVerifier)
    res      <- EitherT(verifier.validate(vk, sig, msg))
  } yield res

  def useInterface[T](label: K)(f: User.Data => Option[T])(ff: T => Boolean): Boolean =
    interfaces
      .get(label)
      .flatMap { in =>
        in.parse[T](f).map { value =>
          ff(value)
        }
      }
      .fold(false)(identity)

//  def MustInclude() = ???

  def exactMatch(label: K, compareTo: Array[Byte]): Boolean =
    useInterface(label)(d => Some(d.bytes))(b => b sameElements compareTo)

  def lessThan(label: K, compareTo: Long): Boolean =
    useInterface(label)(d => Some(BigInt(d.bytes)))(n => n.longValue < compareTo)

  def greaterThan(label: K, compareTo: Long): Boolean =
    useInterface(label)(d => Some(BigInt(d.bytes)))(n => n.longValue > compareTo)

  def equalTo(label: K, compareTo: Long): Boolean =
    useInterface(label)(d => Some(BigInt(d.bytes)))(n => n.longValue == compareTo)
}

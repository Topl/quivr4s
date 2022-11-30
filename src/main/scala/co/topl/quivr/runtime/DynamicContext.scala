package co.topl.quivr.runtime

import cats.Monad
import cats.data.EitherT
import co.topl.common.Models.{DigestVerification, SignatureVerification}
import co.topl.common.{Data, ParsableDataInterface}
import co.topl.quivr.SignableBytes
import co.topl.quivr.algebras.{DigestVerifier, SignatureVerifier}
import co.topl.quivr.runtime.QuivrRuntimeErrors.{ContextError, ValidationError}

trait DynamicContext[F[_], K] {
  val datums: Map[K, Datum[_]]

  val interfaces: Map[K, ParsableDataInterface[F]]
  val signingRoutines: Map[K, SignatureVerifier[F]]
  val hashingRoutines: Map[K, DigestVerifier[F]]

  def signableBytes: F[SignableBytes]

  def currentTick: F[Long]

  def heightOf(label: K)(implicit monad: Monad[F]): F[Either[QuivrRuntimeError, Long]] =
    EitherT.fromEither[F](
      datums.get(label) match {
        case Some(v: IncludesHeight[_]) => Right(v.height)
        case _                          => Left(ContextError.FailedToFindDatum: QuivrRuntimeError)
      }
    ).value

  def digestVerify(routine: K)(verification: DigestVerification)(implicit
    monad:                  Monad[F]
  ): EitherT[F, QuivrRuntimeError, DigestVerification] = for {
    verifier <- EitherT.fromOption(hashingRoutines.get(routine), ContextError.FailedToFindDigestVerifier)
    res      <- EitherT(verifier.validate(verification))
  } yield res

  def signatureVerify(routine: K)(verification: SignatureVerification)(implicit
    monad:                     Monad[F]
  ): EitherT[F, QuivrRuntimeError, SignatureVerification] = for {
    verifier <- EitherT.fromOption[F](signingRoutines.get(routine), ContextError.FailedToFindSignatureVerifier)
    res      <- EitherT(verifier.validate(verification))
  } yield res

  def useInterface[E, T](label: K)(f: Data => Either[QuivrRuntimeError, T])(ff: T => Boolean)(implicit
    monad:                      Monad[F]
  ): EitherT[F, QuivrRuntimeError, T] =
    EitherT.fromEither[F](
      interfaces.get(label) match {
        case Some(interface) =>
          interface.parse[QuivrRuntimeError, T](f).flatMap { t =>
            Either.cond(ff(t), t, ValidationError.UserProvidedInterfaceFailure)
          }
        case None => Left(ContextError.FailedToFindInterface)
      }
    )

  //  def MustInclude() = ???

  def exactMatch(label: K, compareTo: Array[Byte])(implicit
    monad:              Monad[F]
  ): EitherT[F, QuivrRuntimeError, Array[Byte]] =
    useInterface(label)(d => Right(d.value))(b => b sameElements compareTo)(monad)

  def lessThan(label: K, compareTo: Long)(implicit
    monad:            Monad[F]
  ): EitherT[F, QuivrRuntimeError, Long] =
    useInterface(label)(d => Right(BigInt(d.value).longValue))(n => n < compareTo)

  def greaterThan(label: K, compareTo: Long)(implicit
    monad:               Monad[F]
  ): EitherT[F, QuivrRuntimeError, Long] =
    useInterface(label)(d => Right(BigInt(d.value).longValue))(n => n > compareTo)

  def equalTo(label: K, compareTo: Long)(implicit
    monad:           Monad[F]
  ): EitherT[F, QuivrRuntimeError, Long] =
    useInterface(label)(d => Right(BigInt(d.value).longValue))(n => n == compareTo)
}

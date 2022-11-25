package co.topl.quivr.runtime

import cats.Monad
import cats.data.EitherT
import co.topl.common.Models.{DigestVerification, SignatureVerification}
import co.topl.common.ParsableDataInterface
import co.topl.quivr.SignableBytes
import co.topl.quivr.algebras.{DigestVerifier, SignatureVerifier}
import co.topl.quivr.runtime.QuivrRuntimeErrors.ContextError

trait DynamicContext[F[_], K] {
  val datums: Map[K, Datum]

  val interfaces: Map[K, ParsableDataInterface[F]]
  val signingRoutines: Map[K, SignatureVerifier[F]]
  val hashingRoutines: Map[K, DigestVerifier[F]]

  def signableBytes: F[SignableBytes]

  def currentTick: F[Long]

  def heightOf(label: K)(implicit monad: Monad[F]): EitherT[F, QuivrRuntimeError, Long] =
    EitherT.fromEither[F](
      datums.get(label) match {
        case Some(v: IncludesHeight) => Right(v.height)
        case _                       => Left(ContextError.FailedToFindDatum)
      }
    )

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

//   def useInterface[E, T](label: K)(f: common.Data => Either[E, T])(ff: T => Boolean)(implicit
//     monad:                     Monad[F]
//   ): EitherT[F, E, T] =  for {
//    interface: T <- EitherT.fromOption[F](interfaces.get(label), QErrors.ContextErrors.FailedToFindInterface)
//    decoded <- interface.parse[E, T](f)
//    result <- ff(decoded)
//   } yield ???

// //  def MustInclude() = ???

//   def exactMatch(label: K, compareTo: Array[Byte]): Boolean =
//     useInterface(label)(d => Some(d.bytes))(b => b sameElements compareTo)

//   def lessThan(label: K, compareTo: Long): Boolean =
//     useInterface(label)(d => Some(BigInt(d.bytes)))(n => n.longValue < compareTo)

//   def greaterThan(label: K, compareTo: Long): Boolean =
//     useInterface(label)(d => Some(BigInt(d.bytes)))(n => n.longValue > compareTo)

//   def equalTo(label: K, compareTo: Long): Boolean =
//     useInterface(label)(d => Some(BigInt(d.bytes)))(n => n.longValue == compareTo)
}

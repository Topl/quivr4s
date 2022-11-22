package co.topl.quivr.runtime

import cats.Monad
import cats.data.EitherT
import co.topl.common.{DigestVerification, SignatureVerification}
import co.topl.quivr.SignableTxBytes
import co.topl.quivr.algebras.{DigestVerifier, SignatureVerifier}
import co.topl.quivr.runtime.Errors.ContextErrors

trait DynamicContext[F[_], K] {
  val datums: Map[K, Datum]

  val interfaces: Map[K, Interface[F]]
  val signingRoutines: Map[K, SignatureVerifier[F]]
  val hashingRoutines: Map[K, DigestVerifier[F]]

  def signableBytes: F[SignableTxBytes]

  def currentTick: F[Long]

  def heightOf(label: K)(implicit monad: Monad[F]): EitherT[F, QuivrError, Long] =
    EitherT.fromEither[F](
      datums.get(label) match {
        case Some(v: IncludesHeight) => Right(v.height)
        case _                       => Left(ContextErrors.FailedToFindDatum)
      }
    )

  def digestVerify(routine: K)(verification: DigestVerification)(implicit
    monad:                  Monad[F]
  ): EitherT[F, QuivrError, DigestVerification] = for {
    verifier <- EitherT.fromOption(hashingRoutines.get(routine), ContextErrors.FailedToFindDigestVerifier)
    res      <- EitherT(verifier.validate(verification))
  } yield res

  def signatureVerify(routine: K)(verification: SignatureVerification)(implicit
    monad:                     Monad[F]
  ): EitherT[F, QuivrError, SignatureVerification] = for {
    verifier <- EitherT.fromOption[F](signingRoutines.get(routine), ContextErrors.FailedToFindSignatureVerifier)
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

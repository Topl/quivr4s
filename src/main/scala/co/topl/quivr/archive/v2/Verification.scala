// package co.topl.quivr.v2

// import cats._
// import cats.data.OptionT
// import cats.implicits._
// import co.topl.crypto.hash.blake2b256

// case class EvaluationContext(
//     header: Int,
//     body: Int,
//     transaction: (Int, Array[Byte])
// )

// object Verification {

//   def digest[F[_]: Applicative](
//       proposition: Propositions.Digest,
//       proof: Proofs.Digest
//   )(implicit ctx: EvaluationContext): F[Boolean] = for {
//     _ <- (blake2b256.hash(ctx.transaction._2 :+ Propositions.Digest.token) sameElements proof.witness).pure[F]
//     _ <- (blake2b256.hash(proof.preimage).value sameElements proposition.digest).pure[F]
//   } yield ???

// }

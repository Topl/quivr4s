package co.topl.quivr.interpreters

import co.topl.quivr
import co.topl.quivr.{Digest, Proof}
import co.topl.quivr.algebras.ProverAlgebra
import co.topl.quivr.interpreters.UnspentTransactionOutput.SomeContext

// object Proofs {
//   case class Bool(eval: Boolean) extends Proof[Boolean]
//   case class Num(eval: Int) extends Proof[Int]
//   case class Signature(eval: quivr.Signature) extends Proof[quivr.Signature]

//   case class Or(left: Proofs.Bool, right: Proofs.Bool) extends Proof[Boolean] {
//     val eval: Boolean = left.eval || right.eval
//   }

//   case class And(left: Proofs.Bool, right: Proofs.Bool) extends Proof[Boolean] {
//     val eval: Boolean = left.eval && right.eval
//   }

//   case class Not(bool: Proofs.Bool) extends Proof[Boolean] {
//     val eval: Boolean = !bool.eval
//   }

//   case class EqualTo(left: Proofs.Bool, right: Proofs.Bool) extends Proof[Boolean] {
//     val eval: Boolean = left.eval == right.eval
//   }

//   case class GreaterThan(left: Proofs.Bool, right: Proofs.Bool) extends Proof[Boolean] {
//     val eval: Boolean = left.eval > right.eval
//   }

//   case class LessThan(left: Proofs.Bool, right: Proofs.Bool) extends Proof[Boolean] {
//     val eval: Boolean = left.eval < right.eval
//   }

//   case class Sum(left: Proofs.Num, right: Proofs.Num) extends Proof[Int] {
//     val eval: Int = left.eval + right.eval
//   }

//   implicit val simpleExprAlg: ProverAlgebra[Proof] = new ProverAlgebra[Proof] {
//     override def bool(boolean: Boolean): Proofs.Bool = Proofs.Bool(boolean)
//     override def num(int: Int): Proofs.Num = Proofs.Num(int)
//     override def signature(sk: quivr.SecretKey): Proofs.Signature =
//       Proofs.Signature(Signers.signingAlgebra.sign(sk, ???).eval)

//     override def or(left: Proof[Boolean], right: Proof[Boolean]): Proof[Boolean] = Proofs.Bool(left.eval || right.eval)
//     override def and(left: Proof[Boolean], right: Proof[Boolean]): Proof[Boolean] = Proofs.Bool(left.eval && right.eval)
//     override def not(boolean: Proof[Boolean]): Proof[Boolean] = Proofs.Bool(!boolean.eval)

//     override def equalTo(left: Proof[Int], right: Proof[Int]): Proof[Boolean] = Proofs.Bool(left.eval == right.eval)
//     override def greaterThan(left: Proof[Int], right: Proof[Int]): Proof[Boolean] = Proofs.Bool(left.eval > right.eval)
//     override def lessThan(left: Proof[Int], right: Proof[Int]): Proof[Boolean] = Proofs.Bool(left.eval < right.eval)

//     override def sum(left: Proof[Int], right: Proof[Int]): Proof[Int] = Proofs.Num(left.eval + right.eval)
//   }
// }

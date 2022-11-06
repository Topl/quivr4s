package co.topl.quivr.interpreters

import co.topl.quivr
import co.topl.quivr.Proposal
import co.topl.quivr.algebras.ProposerAlgebra

// object Propositions {
//   case class Bool(eval: Boolean) extends Proposal[Boolean]
//   case class Num(eval: Int) extends Proposal[Int]
//   case class Signature(eval: quivr.VerificationKey) extends Proposal[quivr.VerificationKey]

//   case class Or(left: Propositions.Bool, right: Propositions.Bool) extends Proposal[Boolean] {
//     val eval: Boolean = left.eval || right.eval
//   }

//   case class And(left: Propositions.Bool, right: Propositions.Bool) extends Proposal[Boolean] {
//     val eval: Boolean = left.eval && right.eval
//   }

//   case class Not(bool: Propositions.Bool) extends Proposal[Boolean] {
//     val eval: Boolean = !bool.eval
//   }

//   case class EqualTo(left: Propositions.Bool, right: Propositions.Bool) extends Proposal[Boolean] {
//     val eval: Boolean = left.eval == right.eval
//   }

//   case class GreaterThan(left: Propositions.Bool, right: Propositions.Bool) extends Proposal[Boolean] {
//     val eval: Boolean = left.eval > right.eval
//   }

//   case class LessThan(left: Propositions.Bool, right: Propositions.Bool) extends Proposal[Boolean] {
//     val eval: Boolean = left.eval < right.eval
//   }

//   case class Sum(left: Propositions.Num, right: Propositions.Num) extends Proposal[Int] {
//     val eval: Int = left.eval + right.eval
//   }

//   implicit val simpleExprAlg: ProposerAlgebra[Proposal] = new ProposerAlgebra[Proposal] {
//     override def bool(boolean: Boolean): Propositions.Bool = Propositions.Bool(boolean)
//     override def num(int: Int): Propositions.Num = Propositions.Num(int)
//     override def signature(vk: quivr.VerificationKey): Propositions.Signature = Propositions.Signature(vk)

//     override def or(left: Proposal[Boolean], right: Proposal[Boolean]): Proposal[Boolean] = Propositions.Bool(left.eval || right.eval)
//     override def and(left: Proposal[Boolean], right: Proposal[Boolean]): Proposal[Boolean] = Propositions.Bool(left.eval && right.eval)
//     override def not(boolean: Proposal[Boolean]): Proposal[Boolean] = Propositions.Bool(!boolean.eval)

//     override def equalTo(left: Proposal[Int], right: Proposal[Int]): Proposal[Boolean] = Propositions.Bool(left.eval == right.eval)
//     override def greaterThan(left: Proposal[Int], right: Proposal[Int]): Proposal[Boolean] = Propositions.Bool(left.eval > right.eval)
//     override def lessThan(left: Proposal[Int], right: Proposal[Int]): Proposal[Boolean] = Propositions.Bool(left.eval < right.eval)

//     override def sum(left: Proposal[Int], right: Proposal[Int]): Proposal[Int] = Propositions.Num(left.eval + right.eval)
//   }
// }

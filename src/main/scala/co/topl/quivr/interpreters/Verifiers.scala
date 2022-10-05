package co.topl.quivr.interpreters

import co.topl.quivr
import co.topl.quivr.algebras.VerifierAlgebra
import co.topl.quivr.{Proof, Proposal, Verification}

object Verifiers {
  case class Bool(eval: Boolean) extends Verification[Boolean]
  case class Num(eval: Int) extends Verification[Int]
  case class Signature(vk: Proposal[quivr.VerificationKey], msg: Array[Byte], sig: Proof[quivr.Signature]) extends Verification[Boolean] {
    val eval: Boolean = Signers.signingAlgebra.verify(vk.eval, msg, sig.eval).eval
  }

  case class Or(left: Verifiers.Bool, right: Verifiers.Bool) extends Verification[Boolean] {
    val eval: Boolean = left.eval || right.eval
  }

  case class And(left: Verifiers.Bool, right: Verifiers.Bool) extends Verification[Boolean] {
    val eval: Boolean = left.eval && right.eval
  }

  case class Not(bool: Verifiers.Bool) extends Verification[Boolean] {
    val eval: Boolean = !bool.eval
  }

  case class EqualTo(left: Verifiers.Bool, right: Verifiers.Bool) extends Verification[Boolean] {
    val eval: Boolean = left.eval == right.eval
  }

  case class GreaterThan(left: Verifiers.Bool, right: Verifiers.Bool) extends Verification[Boolean] {
    val eval: Boolean = left.eval > right.eval
  }

  case class LessThan(left: Verifiers.Bool, right: Verifiers.Bool) extends Verification[Boolean] {
    val eval: Boolean = left.eval < right.eval
  }

  case class Sum(left: Verifiers.Num, right: Verifiers.Num) extends Verification[Int] {
    val eval: Int = left.eval + right.eval
  }

  implicit val simpleExprAlg: VerifierAlgebra[Verification] = new VerifierAlgebra[Verification] {
    override def bool(boolean: Boolean): Verifiers.Bool = Verifiers.Bool(boolean)
    override def num(int: Int): Verifiers.Num = Verifiers.Num(int)
    override def signature(vk: Proposal[quivr.VerificationKey], msg: Array[Byte], sig: Proof[quivr.Signature]): Verification[Boolean] =
      Verifiers.Bool(Signers.signingAlgebra.verify(vk.eval, msg, sig.eval).eval)

    override def or(left: Verification[Boolean], right: Verification[Boolean]): Verification[Boolean] = Verifiers.Bool(left.eval || right.eval)
    override def and(left: Verification[Boolean], right: Verification[Boolean]): Verification[Boolean] = Verifiers.Bool(left.eval && right.eval)
    override def not(boolean: Verification[Boolean]): Verification[Boolean] = Verifiers.Bool(!boolean.eval)

    override def equalTo(left: Verification[Int], right: Verification[Int]): Verification[Boolean] = Verifiers.Bool(left.eval == right.eval)
    override def greaterThan(left: Verification[Int], right: Verification[Int]): Verification[Boolean] = Verifiers.Bool(left.eval > right.eval)
    override def lessThan(left: Verification[Int], right: Verification[Int]): Verification[Boolean] = Verifiers.Bool(left.eval < right.eval)

    override def sum(left: Verification[Int], right: Verification[Int]): Verification[Int] = Verifiers.Num(left.eval + right.eval)
  }
}

package co.topl.quivr.archive.algebras

import cats.Functor

sealed trait QuivrContractExpr[+A] extends Functor[QuivrContractExpr] {

  def map[B](f: A => B): QuivrContractExpr[B] = map(this)(f)

  def zip[B](that: QuivrContractExpr[B]): QuivrContractExpr[(A, B)] =
    (this, that) match {
      case (False, _)                 => False
      case (_, False)                 => False
      case (Not(x), Not(y))           => Not(x.zip(y))
      case (And(l0, r0), And(l1, r1)) => And(l0.zip(l1), r0.zip(r1))
      case (Or(l0, r0), Or(l1, r1))   => Or(l0.zip(l1), r0.zip(r1))
      case (Threshold(threshold0, props0), Threshold(threshold1, props1)) =>
        if (threshold0 != threshold1) False
        else Threshold(threshold0, props0.zip(props1).map(e => e._1.zip(e._2)))
      case (HeightLock(height0), HeightLock(height1)) =>
        if (height0 != height1) False
        else HeightLock(height0)
      case (Signature(signObject0), Signature(signObject1)) =>
        Signature((signObject0, signObject1))
      case (_, _) => False
    }

  override def map[A, B](fa: QuivrContractExpr[A])(
      f: A => B
  ): QuivrContractExpr[B] = fa match {
    case False     => False
    case Not(e)    => Not(fa.map(e)(f))
    case And(l, r) => And(fa.map(l)(f), fa.map(r)(f))
    case Or(l, r)  => Or(fa.map(l)(f), fa.map(r)(f))
    case Threshold(threshold, props) =>
      Threshold(threshold, props.map(fa.map(_)(f)))
    case HeightLock(height)    => HeightLock(height)
    case Signature(signObject) => Signature(f(signObject))
  }

}

case object False extends QuivrContractExpr[Nothing]

case class Not[A](expr: QuivrContractExpr[A]) extends QuivrContractExpr[A]

case class And[A](left: QuivrContractExpr[A], right: QuivrContractExpr[A])
    extends QuivrContractExpr[A]

case class Or[A](left: QuivrContractExpr[A], right: QuivrContractExpr[A])
    extends QuivrContractExpr[A]

case class Threshold[A](
    threshold: Int,
    propositions: List[QuivrContractExpr[A]]
) extends QuivrContractExpr[A]

case class HeightLock[A](height: Long) extends QuivrContractExpr[A]

case class Signature[A](signObject: A) extends QuivrContractExpr[A]

case class IndexedSignature(idx: Int)

trait QuivrContractAlgebra[A] {

  def not(expr: QuivrContractExpr[A]): QuivrContractExpr[A] =
    Not(expr)

  def or(
      left: QuivrContractExpr[A],
      right: QuivrContractExpr[A]
  ): QuivrContractExpr[A] = Or(left, right)

  def and(
      left: QuivrContractExpr[A],
      right: QuivrContractExpr[A]
  ): QuivrContractExpr[A] = And(left, right)

  def threshold(
      threshold: Int,
      propositions: List[QuivrContractExpr[A]]
  ): QuivrContractExpr[A] = Threshold(threshold, propositions)

  def heightLock(height: Long): QuivrContractExpr[A] = HeightLock(height)
}

object QuivrContractAlgebraSyntax
    extends QuivrContractAlgebra[IndexedSignature] {
  def signature(idx: Int): QuivrContractExpr[IndexedSignature] = Signature(
    IndexedSignature(idx)
  )

}

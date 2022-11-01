package co.topl.quivr

import cats.free.Free

package object algebras {

  type QuivrContractExprFree[A] = Free[QuivrContractExpr, A]

}

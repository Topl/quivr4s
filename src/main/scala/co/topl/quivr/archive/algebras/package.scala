package co.topl.quivr.archive

import cats.free.Free

package object algebras {

  type QuivrContractExprFree[A] = Free[QuivrContractExpr, A]

}

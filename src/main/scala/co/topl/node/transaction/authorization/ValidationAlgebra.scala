package co.topl.node.transaction.authorization

import co.topl.brambl.models.transaction.IoTransaction
import co.topl.quivr.runtime.DynamicContext
import co.topl.common.ContextualValidation

trait ValidationAlgebra[F[_]] extends ContextualValidation[F, ValidationError, IoTransaction, DynamicContext[F, String]]

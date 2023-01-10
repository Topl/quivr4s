package co.topl.brambl.transaction.validators.authorization

import co.topl.brambl.models.Datum
import co.topl.brambl.models.transaction.IoTransaction
import co.topl.common.ContextualValidation
import co.topl.quivr.runtime.DynamicContext

trait TransactionAuthorizationVerifier[F[_]]
    extends ContextualValidation[F, TransactionAuthorizationError, IoTransaction, DynamicContext[F, String, Datum]]

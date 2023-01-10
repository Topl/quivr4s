package co.topl.brambl.transaction.validators.syntax

import co.topl.brambl.models.transaction.IoTransaction
import co.topl.common.ContextlessValidation

trait TransactionSyntaxVerifier[F[_]] extends ContextlessValidation[F, TransactionSyntaxError, IoTransaction]

import co.topl.quivr.Quivr

import java.security.SecureRandom

val hashF = (x: Array[Byte]) => co.topl.crypto.hash.blake2b256.hash(x)
var txBytes = Array.fill(32)(0: Byte)
SecureRandom.getInstanceStrong().nextBytes(txBytes)

val ctx = Quivr.EvaluationContext(
  Quivr.Datums.Header(1L, 10L),
  Quivr.Datums.Body(Array(0: Byte)),
  Quivr.Datums.IoTx(txBytes),
  Quivr.Datums.Box(Array(0: Byte))
)

val img_0 = "g".getBytes()
val pi_0 = hashF(img_0)
val C_0 = Models.Primitive.Digest.Proposition(pi_0.value)
val R_0 = Models.Primitive.Digest.Proof(
  img_0,
  Quivr.Prover.bind(Models.Primitive.Digest.token, txBytes)
)
val eval_0 = Quivr.Verifier.digestVerifier[Option].verify(C_0, R_0)(ctx)

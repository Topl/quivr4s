package co.topl.brambl.digests

import co.topl.common.Models.{Digest, Preimage}

trait Hash {
  val routine: String
  def hash(preimage: Preimage): Digest
}

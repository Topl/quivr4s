package co.topl.brambl.digests

import quivr.models._

trait Hash {
  val routine: String
  def hash(preimage: Preimage): Digest
}

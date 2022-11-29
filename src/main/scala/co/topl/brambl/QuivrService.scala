package co.topl.brambl

import co.topl.quivr.Models.Primitive.Digest
import co.topl.quivr.{Proposition, Proof}

// Easy to use Topl-opinionated layer for Brambl to use to access the un-opinionated quivr API

object QuivrService {
  def digestProposition: Digest.Proposition = ???
  private def digestProof: Digest.Proof = ???

  // take in any proposition and return a corresponding proof??
  def prove(prop: Proposition): Proof = ???
}

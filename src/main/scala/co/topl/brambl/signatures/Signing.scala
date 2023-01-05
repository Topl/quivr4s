package co.topl.brambl.signatures

import co.topl.brambl.Models.{KeyPair, SigningKey}
import quivr.models._

trait Signing {
  val routine: String
  def createKeyPair(seed: Array[Byte]): KeyPair
  def sign(sk:            SigningKey, msg: SignableBytes): Witness
}

package co.topl

object Crypto {
  trait SecretKey
  trait VerificationKey
  trait Witness

  object DigitalSignatures {
    case class SecretKey(bytes: Array[Byte]) extends Crypto.SecretKey
    case class VerificationKey(bytes: Array[Byte]) extends Crypto.SecretKey
    case class Witness(bytes: Array[Byte]) extends Crypto.Witness
  }
}

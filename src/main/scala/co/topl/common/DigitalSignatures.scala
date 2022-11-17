package co.topl.common

  trait SecretKey
  trait VerificationKey
  trait Witness

  object DigitalSignatures {
    case class SecretKey(bytes: Array[Byte]) extends SecretKey
    case class VerificationKey(bytes: Array[Byte]) extends VerificationKey
    case class Witness(bytes: Array[Byte]) extends Witness
  }
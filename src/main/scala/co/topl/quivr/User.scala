package co.topl.quivr

object User {
  trait SecretKey
  trait VerificationKey
  trait Witness
  trait Preimage
  trait Digest

  abstract class Data(val bytes: Array[Byte])

  object DigitalSignatures {
    case class SecretKey(bytes: Array[Byte]) extends User.SecretKey
    case class VerificationKey(bytes: Array[Byte]) extends User.SecretKey
    case class Witness(bytes: Array[Byte]) extends User.Witness
  }

  object Digests {
    case class Preimage(bytes: Array[Byte]) extends User.Preimage
    case class Digest(length: Byte, digest: Array[Byte]) extends User.Digest
  }
}

package co.topl

package object node {
  object Models {
    trait Blob

    type Metadata = Option[Array[Byte]]
    type Root = Array[Byte]
    type SignableBytes = Array[Byte] // MUST NOT include proof bytes
    type IdentifiableBytes = Array[Byte] // hash(SignableBytes)
  }

}

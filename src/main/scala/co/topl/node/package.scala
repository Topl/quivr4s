package co.topl

package object node {
  type SmallData = Array[Byte] // small, up to 64 bytes
  type Root = Array[Byte] // fixed size 32 or 64 bytes as a root from an accumulator
  type IdentifiableBytes = Array[Byte] // hash(SignableBytes)
}

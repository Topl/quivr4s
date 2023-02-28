package co.topl.quivr.generators

import com.google.protobuf.ByteString
import org.scalacheck.{Arbitrary, Gen}
import quivr.models.Digest.{Digest32, Digest64}

trait ModelGenerators {

  def genSizedStrictByteString(n: Int)(
    byteGen: Gen[Byte] = Gen.choose[Byte](0, 32)
  ): Gen[ByteString] =
    Gen
      .containerOfN[Array, Byte](n, byteGen)
      .map(ByteString.copyFrom)

  val arbitraryDigest32: Arbitrary[Digest32] =
    Arbitrary(
      for {
        bs <- genSizedStrictByteString(32)()
      } yield Digest32(bs)
    )

  val arbitraryDigest64: Arbitrary[Digest64] =
    Arbitrary(
      for {
        bs <- genSizedStrictByteString(64)()
      } yield Digest64(bs)
    )

}

object ModelGenerators extends ModelGenerators

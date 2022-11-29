package co.topl.node.typeclasses

import co.topl.crypto.accumulators.LeafData
import co.topl.crypto.accumulators.merkle.MerkleTree
import co.topl.crypto.hash.digest.{Digest32, Digest64}
import co.topl.crypto.hash.{blake2b256, blake2b512, Blake2b}
import co.topl.crypto.implicits.{blake2b256Hash, blake2b512Hash, digestDigest32, digestDigest64}
import co.topl.node.Evidence

trait ContainsEvidence[T] {
  val value: T
  val sized32Evidence: Evidence[Digest32]
  val sized64Evidence: Evidence[Digest64]
}

object ContainsEvidence {
  def apply[T](t: T)(implicit ev: ContainsEvidence[T]): ContainsEvidence[T] = ev

  implicit class SignableOps[T: ContainsSignable](t: T) {
    def blake2bEvidence: ContainsEvidence[T] = blake2bEvidenceFromSignable[T](t)
  }

  def blake2bEvidenceFromSignable[T: ContainsSignable](t: T): ContainsEvidence[T] =
    new ContainsEvidence[T] {
      override val value: T = t

      override val sized32Evidence: Evidence[Digest32] =
        Evidence.Sized32(
          blake2b256.hash(
            ContainsSignable[T].signableBytes(value)
          )
        )

      override val sized64Evidence: Evidence[Digest64] =
        Evidence.Sized64(
          blake2b512.hash(
            ContainsSignable[T].signableBytes(t)
          )
        )
    }

  implicit class ListOps[T: ContainsSignable](t: List[T]) {
    def merkleEvidence: ContainsEvidence[List[T]] = merkleRootFromBlake2bEvidence[T](t)
  }

  def merkleRootFromBlake2bEvidence[T: ContainsSignable](list: List[T]): ContainsEvidence[List[T]] =
    new ContainsEvidence[List[T]] {
      override val value: List[T] = list

      override val sized32Evidence: Evidence[Digest32] =
        Evidence.Sized32(
          MerkleTree
            .apply[Blake2b, Digest32](
              list.zipWithIndex
                .map { case (item, index) => LeafData(ContainsSignable[T].signableBytes(item)) }
            )
            .rootHash
        )

      override val sized64Evidence: Evidence[Digest64] =
        Evidence.Sized64(
          MerkleTree
            .apply[Blake2b, Digest64](
              list.zipWithIndex
                .map { case (item, index) => LeafData(ContainsSignable[T].signableBytes(item)) }
            )
            .rootHash
        )
    }
}

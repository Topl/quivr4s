package co.topl.node.typeclasses

import co.topl.node.Root
import co.topl.node.transaction.Blob

trait ContainsRoot[T] {
  def rootOf(t: List[Option[T]]): Root
}

object ContainsRoot {
  def apply[T](implicit ev: ContainsRoot[T]): ContainsRoot[T] = ev

  implicit class Ops[T: ContainsRoot](t: List[Option[T]]) {
    def root: Root = ContainsRoot[T].rootOf(t)
  }

  trait Instances {
    implicit val blobsRoot: ContainsRoot[Blob] = (blobs: List[Option[Blob]]) =>
      blobs.zipWithIndex.foldLeft(Array[Byte]()) {
        case (acc, (Some(blob), index)) => acc ++ BigInt(index).toByteArray ++ blob.value
        case (acc, (_, index)) => acc ++ BigInt(index).toByteArray
      }
  }
}

object Roots {
  case class Blob(root: Root, value: List[Option[Blob]])
}

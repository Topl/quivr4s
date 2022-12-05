package co.topl.brambl

import cats.Show
import cats.implicits.toShow
import co.topl.node.box.{Lock, Locks}
import co.topl.node.{Address, Identifier, KnownIdentifier}
import co.topl.node.transaction.{Attestation, IoTransaction, SpentTransactionOutput, UnspentTransactionOutput}
import co.topl.quivr.Models.Primitive
import co.topl.quivr.{Proof, Proposition}


object Models {
  case class Indices(x: Int, y: Int, z: Int)
  implicit val showIndices: Show[Indices] = Show.show(idx => s"(${idx.x},${idx.y},${idx.z})")


  implicit val showProposition: Show[Proposition] = Show.show {
    case _: Primitive.Locked.Proposition => "LockedProposition"
    case d: Primitive.Digest.Proposition => s"DigestProposition(${d.digest.value.hashCode}))"
  }
  implicit val showLock: Show[Lock] = Show.show {
    case p: Locks.Predicate => s"Predicate(${p.challenges.map(_.show)},${p.threshold})"
  }
  implicit val showProof: Show[Proof] = Show.show {
    case _: Primitive.Locked.Proof => "LockedProof"
    case d: Primitive.Digest.Proof => s"DigestProof(${d.preimage.input.hashCode})"
    case _ => "???"

  }
  implicit val showOptionProof: Show[Option[Proof]] = Show.show(p => if(p.isDefined) p.show else "X")
  implicit val showAttestation: Show[Attestation] = Show.show(att =>
    s"Att(\n\t${att.lock.show}\n\t${att.responses.map(_.show)}\n)"
  )
  implicit val showIdentifier: Show[Identifier] = Show.show(id => s"Id(${id.tag}: ${id.evidence.value.hashCode})")
  implicit val showAddress: Show[Address] = Show.show(addr =>
    s"Addr(${addr.network},${addr.ledger},${addr.identifier.show})"
  )
  implicit val showKnownIdentifier: Show[KnownIdentifier] = Show.show(known =>
    s"Known(${known.network},${known.ledger},${known.index},${known.id.show})"
  )
  implicit val showInput: Show[SpentTransactionOutput] = Show.show(in =>
    s"Input(\n\tIdentifier: ${in.knownIdentifier.show}\n\tAttestation: ${in.attestation.show}\n\t\n)"
  )
  implicit val showOutput: Show[UnspentTransactionOutput] = Show.show(out =>
    s"Output(\n\taddress: ${out.address.show}\n)"
  )
  implicit val showIoTx: Show[IoTransaction] = Show.show(tx =>
    s"IoTx(\n\tinputs: ${tx.inputs.map(_.show)}\n\toutputs: ${tx.outputs.map(_.show)}\n)"
  )


}

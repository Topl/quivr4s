package co.topl.node.typeclasses

import co.topl.crypto.hash.blake2b256
import co.topl.node.PropositionEvidence
import co.topl.quivr.Models.Compositional._
import co.topl.quivr.Models.Contextual._
import co.topl.quivr.Models.Primitive._
import co.topl.quivr.Proposition

import java.nio.charset.StandardCharsets

trait ProposedEvidence[T] {
  def evidenceOf(t: T): PropositionEvidence
}

object ProposedEvidence {
  def apply[T](implicit ev: ProposedEvidence[T]): ProposedEvidence[T] = ev

  implicit class Ops[T: ProposedEvidence](t: T) {
    def evidence: PropositionEvidence = ProposedEvidence[T].evidenceOf(t)
  }

  private def commit(bytes: Array[Byte]): Array[Byte] = blake2b256.hash(bytes).value

  trait Instances {

    private def lockedEvidence(p: Locked.Proposition): PropositionEvidence =
        commit(
          Locked.token.getBytes(StandardCharsets.UTF_8)
        )

    private def digestEvidence(p: Digest.Proposition): PropositionEvidence =
        commit(
          Digest.token.getBytes(StandardCharsets.UTF_8) ++
          p.routine.getBytes(StandardCharsets.UTF_8) ++
          p.digest.value
        )

    private def signatureEvidence(p: DigitalSignature.Proposition): PropositionEvidence =
        commit(
          DigitalSignature.token.getBytes(StandardCharsets.UTF_8) ++
          p.routine.getBytes(StandardCharsets.UTF_8) ++
          p.vk.value
        )

    private def heightRangeEvidence(p: HeightRange.Proposition): PropositionEvidence =
        commit(
          HeightRange.token.getBytes(StandardCharsets.UTF_8) ++
          p.chain.getBytes(StandardCharsets.UTF_8) ++
          BigInt(p.min).toByteArray ++
          BigInt(p.max).toByteArray
        )

    private def tickRangeEvidence(p: TickRange.Proposition): PropositionEvidence =
        commit(
          TickRange.token.getBytes(StandardCharsets.UTF_8) ++
          BigInt(p.min).toByteArray ++
          BigInt(p.max).toByteArray
        )

    private def thresholdEvidence(p: Threshold.Proposition): PropositionEvidence =
        commit(
          Threshold.token.getBytes(StandardCharsets.UTF_8) ++
            BigInt(p.threshold).toByteArray ++
            p.challenges.zipWithIndex.foldLeft(Array[Byte]()) {
              case (acc, (challenge, index)) =>
                acc ++
                BigInt(index).toByteArray ++
                challenge.evidence
            }
        )

    private def notEvidence(p: Not.Proposition): PropositionEvidence =
        commit(
          Not.token.getBytes(StandardCharsets.UTF_8) ++
          p.proposition.evidence
        )

    private def andEvidence(p: And.Proposition): PropositionEvidence =
        commit(
          And.token.getBytes(StandardCharsets.UTF_8) ++
          p.left.evidence ++
          p.right.evidence
        )

    private def orEvidence(p: Or.Proposition): PropositionEvidence =
        commit(
          Or.token.getBytes(StandardCharsets.UTF_8) ++
            p.left.evidence ++
            p.right.evidence
        )

    implicit val propositionEvidence: ProposedEvidence[Proposition] = {
      case p: Locked.Proposition           => lockedEvidence(p)
      case p: Digest.Proposition           => digestEvidence(p)
      case p: DigitalSignature.Proposition => signatureEvidence(p)
      case p: HeightRange.Proposition      => heightRangeEvidence(p)
      case p: TickRange.Proposition        => tickRangeEvidence(p)
      case p: ExactMatch.Proposition       => ???
      case p: LessThan.Proposition         => ???
      case p: GreaterThan.Proposition      => ???
      case p: EqualTo.Proposition          => ???
      case p: Threshold.Proposition        => thresholdEvidence(p)
      case p: Not.Proposition              => notEvidence(p)
      case p: And.Proposition              => andEvidence(p)
      case p: Or.Proposition               => orEvidence(p)
    }
  }

  object instances extends Instances
}

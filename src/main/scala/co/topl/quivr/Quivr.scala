package co.topl.quivr

object Quivr {

  trait Validation[S, C, R, M] {
    // the function used to determine whether to pass
    def eval(value: S): Option[S]
  }

  //case class BoxState[R, W](write: W, read: Set[R])


  case class Proposition[C](constructor: C)
  case class Proof[S, C, R, M](constructor: R, message: M)
  case class Message[S](state: S)

//  class Commit[F[_]]
//  object Commit {
//    def apply(proposition: Proposition[_]): Commit[Proposition[_]] = new Commit()
//  }

//  case class Stxo[C, R, S](spend: Utxo[C, S], proof: Proof[C, R, S])
//  case class Utxo[C, S](challenge: Proposition[C], state: S)
//
//  case class Transaction[C, R, S](inputs: List[Stxo[C, R, S]], outputs: List[Utxo[C, S]])

  trait SigmaProtocolValidation[S, C, R, M] {
    // the function used to determine whether to pass
    def verify(value: S): Option[S]

    // create a proposition for an instance of this protocol
    def challenge(input: C): Proposition[C] = Proposition(input)

    // create a proof that may satisfy the proposition for an instance of this protocol
    def response(input: R, message: M): Proof[S, C, R, M] = Proof(input, message)

//    // create a commitment to an addressable "program" of this protocol
//    def commit(challengeInput: C): Commit[Proposition[_]] = Commit.apply(Proposition(challengeInput))
  }

//  // define the standard operation of comparing Propositions via their commitment
//  private def compareProposition(challengeInput: C)(other: Commit[Proposition[_]]): Boolean =
//    commit(challengeInput) == other

  // components of the auth are
  // - compare contract in stxo to commitment in utxo
  // - run the contract predicate to ensure the user is authorized
  // - ensure the message semantically valid within the context and scope of policies that define the tra

  // define
  //private def comparePredicate()

  object EqualTo {
    object Constructors {
      case class Challenge(value: Int)
      case class Response()
    }

//    case class EqualToSigmaProtocol[S](challenge: Constructors.Challenge,
//                                       response: Constructors.Response,
//                                       message: Message[S]
//                                      ) extends SigmaProtocol[Constructors.Challenge, S, Constructors.Response, Message[S]] {
//      override def verify(value: S): Option[S] = value match {
//        case v: Int => if (challenge.value == v & v < 5)
//      }
//    }
//
//    object ValueMutations {
//      def evaluateOutput_0(valueIn: Int): Boolean
//    }
  }





  //case class Challenges[C, S](value: Set[Proposition[C, S]])
  //case class Attestations[C, S](value: Set[Proof[C, S]])


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//  case class Contract[C, S](challenges: Set[Proposition[C, S]], attestations: Set[Proof[C, S]], threshold: Int)
//
//  trait ContractVM[C, S] {
//    def verify(verifierChallenge: Proposition[C, S], proverResponse: Proof[C, S]): Option[S]
//
//    // this isn't an efficient way to do the comparison to the threshold but it is easy
//    // should recreate with an iterator or discuss other approaches
//    def isSufficientlySatisfied(contract: Contract[C, S]): Option[S]  = for {
//      leafResults <- contract.challenges.foldLeft(0) {
//        case (countValidProofs, currentExposure) =>
//          verify(currentExposure.proposition, currentExposure.proof) match {
//            case Some(value) =>
//            case None => countValidProofs
//          }
//          if (verify(currentExposure.proposition, currentExposure.proof)) countValidProofs + 1
//          else countValidProofs
//      }
//
//      //=> verify(c.proposition, c.proof) }
//      //overallResult <- if (leafResults.challenges.size >= )
//
//    } yield ???
//
//
//  }

//  sealed abstract class PropositionVM[A] {
//    def verify(verifierChallenge: Proposition[A], proverResponse: Proof): Boolean
//  }
//   trait Proposition[C[_], S] extends Contract[C[_], S] {
  //    val value: S
  //  }
  //
  //  trait Attestation {
  //
  //  }
  //
  //  trait Proof {
  //
  //  }


//



//  def program1[E[_]: Algebra]: E[Boolean] = {
//    or(b(true), and(b(true), b(false)))
//  }
//
//  def program2[E[_]](using alg: Algebra[E]): E[Int] = {
//    import alg._
//    sum(i(24), i(-3))
//  }
//
//  def main(args: Array[String]): Unit = {
//
//  }

}

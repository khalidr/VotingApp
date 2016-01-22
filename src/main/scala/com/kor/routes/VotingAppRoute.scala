package com.kor.routes

import com.kor.domain._
import com.kor.repo.Repository
import com.kor.services.FileSharingService
import com.typesafe.scalalogging.StrictLogging
import spray.http.StatusCodes
import spray.httpx.PlayJsonSupport
import spray.routing.Directives
import scalaz.syntax.std.boolean._

import scala.concurrent.ExecutionContext

trait VotingAppRoute extends Directives with PlayJsonSupport with StrictLogging{

  def route(repo:Repository, sharingService:FileSharingService)(implicit ec:ExecutionContext) =
    path("event"){
      pathEndOrSingleSlash{
        post {
          implicit val r = IncomingMessage.reads
          entity(as[IncomingMessage]) {
            case TextMessage(msg) =>
              onSuccess(sharingService.fileExists(msg)){exists =>
                exists.fold({
                  onSuccess(addVote(repo, msg)) { _ =>
                    complete(StatusCodes.OK)
                  }
                }, {
                  complete(StatusCodes.NotFound)
                })
              }
            case MediaMessage(url) =>
              onSuccess(sharingService.upload(url)) { resp =>
                  complete(resp.status)
              }
            case e:VoiceMessage => complete(StatusCodes.BadRequest, "Voice messages not handled.")
          }
        }
      }
    } ~
    path("report"){
      get{
        complete(repo.getAll)
      }
    }

  def addVote(repo:Repository, fileName:String)(implicit ec:ExecutionContext) = {
    val voteF = repo.getByName(fileName)

    for{
      maybeVote <- voteF
      vote = maybeVote.map(v => v.copy(votes = v.votes +1 )).getOrElse(Vote(fileName, 1))
      r <- repo.upsert(vote)
    } yield r
  }
}



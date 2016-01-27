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
          entity(as[IncomingMessage]) {
            case TextMessage(picName) =>
              onSuccess(sharingService.fileExists(picName)){ exists =>
                exists.fold({
                  onSuccess(addVote(repo, picName)) { _ =>
                    complete{
                      logger.info(s"added vote for picture $picName")
                      StatusCodes.OK}
                  }
                }, {
                  complete(StatusCodes.NotFound, s"Picture $picName not found")
                })
              }
            case MediaMessage(url) =>
              onSuccess(sharingService.upload(url)) { resp =>
                  complete(resp.status)
              }
            case _ => complete(StatusCodes.BadRequest, "Message not handled.")
          }
        }
      }
    } ~
    path("report") {
      complete(repo.getAll)
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



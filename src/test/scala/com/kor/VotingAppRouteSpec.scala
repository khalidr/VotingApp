package com.kor

import com.kor.domain.Vote
import com.kor.repo.{OK, Repository}
import com.kor.routes.VotingAppRoute
import com.kor.services.{FileSharingService, RouteSpec}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import spray.http.StatusCodes

import scala.concurrent.Future

class VotingAppRouteSpec extends RouteSpec with MockitoSugar with VotingAppRoute{

  "BurnderAppRoute" must "accept MediaMessages" in new Context{

    val json = Json.parse(
      """{ "type": "inboundText", "payload": "hello.png", "fromNumber": "+12222222222", "toNumber": "+13333333333" }""")
    val existingVote = Vote("hello.png", 1)

    when(fileService.fileExists(any())).thenReturn(Future.successful(true))
    when(repo.getByName(any())).thenReturn(Future.successful(Some(existingVote)))
    when(repo.upsert(any())).thenReturn(Future.successful(OK))

    Post("/event", json) ~> route(repo,fileService) ~> check {
      status mustBe StatusCodes.OK
    }
  }

  it must "report votes" in new Context{
    val votes = List(Vote("img1.png",1), Vote("img2.png",2))
    when(repo.getAll).thenReturn(Future.successful(votes))

    Get("/report") ~> route(repo, fileService) ~> check {
      status mustBe StatusCodes.OK
      responseAs[List[Vote]] must contain theSameElementsAs votes
    }
  }

  trait Context{
    val repo = mock[Repository]
    val fileService = mock[FileSharingService]
  }
}

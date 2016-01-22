package com.kor.actors

import akka.io.IO
import com.kor.repo.Repository
import com.kor.routes.VotingAppRoute
import com.kor.services.FileSharingService
import spray.can.Http
import spray.routing.HttpServiceActor

class WebserviceActor(host:String, port:Int, sharingService:FileSharingService, repo:Repository)
  extends HttpServiceActor with VotingAppRoute {

  IO(Http)(context.system) ! Http.Bind(listener = self, interface = host, port = port)
  import context.dispatcher  //using default pool is not always the best solution

  def receive = runRoute(route(repo, sharingService))
}

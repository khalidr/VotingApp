package com.kor

import java.net.URL

import akka.actor.{ActorSystem, Props}
import com.kor.actors.WebserviceActor
import com.kor.repo.VoteRepo
import com.kor.services.DropBoxService
import com.typesafe.scalalogging.StrictLogging
import reactivemongo.api.MongoDriver

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App with StrictLogging{

  implicit val actorSystem = ActorSystem()  //using default pool is not always the best solution

  val fileShareService = new DropBoxService(new URL(Settings.dropBoxUploadUrl), new URL(Settings.dropBoxMetaDataUrl), Settings.dropBoxToken)
  val mongoDriver = new MongoDriver()
  val mongoConnection = mongoDriver.connection(List(s"${Settings.dbHost}:${Settings.dbPort}"))
  val repo = new VoteRepo(mongoConnection, Settings.dbName)

  actorSystem.actorOf(Props(new WebserviceActor(Settings.host, Settings.port, fileShareService, repo)))

  logger.info(s"Listening on ${Settings.port}")

  sys.addShutdownHook {
    actorSystem.log.info("Shutting down")
    actorSystem.terminate()
    Await.result(actorSystem.whenTerminated, Duration.Inf)
    logger.info(s"Actor system '${actorSystem.name}' successfully shut down")
  }
}

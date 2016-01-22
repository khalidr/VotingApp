package com.kor

import java.net.URL

import akka.actor.{Props, ActorSystem}
import com.kor.actors.WebserviceActor
import com.kor.repo.VoteRepo
import com.kor.services.DropBoxService
import com.typesafe.scalalogging.StrictLogging
import reactivemongo.api.MongoDriver

object Main extends App with StrictLogging{

  implicit val actorSystem = ActorSystem()
  import actorSystem.dispatcher  //using default pool is not always the best solution

  val fileShareService = new DropBoxService(new URL(Settings.dropBoxUploadUrl), new URL(Settings.dropBoxMetaDataUrl), Settings.dropBoxToken)
  val mongoDriver = new MongoDriver()
  val mongoConnection = mongoDriver.connection(List(s"${Settings.dbHost}:${Settings.dbPort}"))
  val repo = new VoteRepo(mongoConnection, Settings.dbName)

  actorSystem.actorOf(Props(new WebserviceActor(Settings.host, Settings.port, fileShareService, repo)))

  sys.addShutdownHook {
    actorSystem.log.info("Shutting down")
    actorSystem.shutdown()
    actorSystem.awaitTermination()
    logger.info(s"Actor system '${actorSystem.name}' successfully shut down")
  }
}

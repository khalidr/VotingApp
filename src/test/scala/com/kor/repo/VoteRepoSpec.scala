package com.kor.repo

import java.util.UUID

import com.kor.domain.Vote
import com.kor.services.UnitSpec
import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, RuntimeConfigBuilder}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{Command, MongodStarter}
import de.flapdoodle.embed.process.config.io.ProcessOutput
import org.scalatest.BeforeAndAfterAll
import org.scalatest.time.{Seconds, Span}
import reactivemongo.api.{MongoConnection, MongoDriver}

import scala.concurrent.ExecutionContext.Implicits.global


class VoteRepoSpec extends UnitSpec with BeforeAndAfterAll{

  private var connection:MongoConnection = _
  override implicit val patienceConfig = PatienceConfig(Span(10, Seconds))
  lazy val mongodExecutable = {
    val runtimeConfig = new RuntimeConfigBuilder().defaults(Command.MongoD).processOutput(ProcessOutput.getDefaultInstanceSilent).build
    val mongodConfig = new MongodConfigBuilder().version(Version.Main.V3_0).build

    MongodStarter.getInstance(runtimeConfig).prepare(mongodConfig)
  }
  override protected def beforeAll() = {
    val mongodProcess = mongodExecutable.start
    connection = new MongoDriver().connection(List(s"${mongodProcess.getConfig.net.getServerAddress.getHostAddress}:${mongodProcess.getConfig.net.getPort}"))
  }

  override protected def afterAll() = {
    connection.close()
    mongodExecutable.stop()
  }

  "VoteRepo" must "upsert" in new Context{
    val vote1 = Vote("abc.png", 1)
    val vote2 = vote1.copy(votes = 2)

    val f = for{
      _ <- repo.upsert(vote1)
      _ <- repo.upsert(vote2)
      v <- repo.getByName(vote1.picName)
    } yield v

    whenReady(f){
      _.value mustBe vote2
    }
  }
  it must "get all" in new Context{
    val vote1 = Vote("abc.png", 1)
    val vote2 = Vote("abc1.png", 1)

    whenReady(
    for{
    _ <- repo.upsert(vote1)
    _ <- repo.upsert(vote2)
    all <- repo.getAll
    } yield all){r =>
      r must contain theSameElementsAs List(vote1,vote2)
    }
  }
  trait Context{
    val repo = new VoteRepo(connection, UUID.randomUUID().toString)
  }
}

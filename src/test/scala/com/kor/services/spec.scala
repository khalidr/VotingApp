package com.kor.services

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import spray.testkit.ScalatestRouteTest
import com.typesafe.config.Config

import scala.concurrent.duration._

trait UnitSpec extends FlatSpecLike with MustMatchers with ScalaFutures with OptionValues with EitherValues

abstract class ActorSpec private (_system: ActorSystem) extends TestKit(_system) with UnitSpec with BeforeAndAfterAll {

  def this() = this(ActorSystem(ActorSpec.actorSystemName))
  def this(config: Config) = this(ActorSystem(ActorSpec.actorSystemName, config))

  def shutdown(): Unit = ()

  final override def afterAll(): Unit = {
    shutdown()
    TestKit.shutdownActorSystem(system)
    super.afterAll()
  }

}

object ActorSpec {

  private def actorSystemName =
    Thread.currentThread.getStackTrace.map(_.getClassName)
      .drop(1).dropWhile(_ matches ".*ActorSpec.?$")
      .head.replaceFirst(""".*\.""", "").replaceAll("[^a-zA-Z_0-9]", "-")

}

abstract class RouteSpec extends UnitSpec with ScalatestRouteTest {

  implicit val routeTestTimeout = RouteTestTimeout(2.second)

  def shutdown(): Unit = ()

  final override def afterAll(): Unit = {
    shutdown()
    TestKit.shutdownActorSystem(system)
    super.afterAll()
  }

}

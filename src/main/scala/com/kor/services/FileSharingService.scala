package com.kor.services

import java.net.{URI, URL}
import java.nio.file.Paths

import akka.actor.ActorRefFactory
import com.kor.domain.FileInfo
import com.kor.services.FileSharingService.Response
import com.typesafe.scalalogging.StrictLogging
import spray.client.pipelining._
import spray.http._
import spray.httpx.PlayJsonSupport
import spray.httpx.unmarshalling.FromResponseUnmarshaller

import scala.concurrent.{ExecutionContext, Future}

trait FileSharingService {
  def upload(url:URL):Future[Response]
  def fileExists(path:String):Future[Boolean]
}
object FileSharingService{
  case class Response(status:StatusCode, reason:String)
}

class DropBoxService(uploadUrl:URL, metaDataUrl:URL, token:String)(implicit actorRefFactory: ActorRefFactory, ec: ExecutionContext)
  extends FileSharingService
  with PlayJsonSupport
  with StrictLogging{

  val pipeline = addHeader("Authorization", s"Bearer $token") ~> sendReceive

  def upload(fileUrl:URL) = {
    val encodedFileUrl = encodeUrl(fileUrl)
    val fileName = Paths.get(encodedFileUrl.toString).getFileName
    val url = s"$uploadUrl$fileName"
    val entity = HttpEntity(ContentType(MediaTypes.`application/x-www-form-urlencoded`), s"url=${encodedFileUrl.toString}")

    pipeline(Post(s"${uploadUrl.toString}$fileName", entity)) map {r => Response(r.status, r.entity.asString)}
  }

  private def encodeUrl(url:URL) = new URI(url.getProtocol, url.getUserInfo, url.getHost, url.getPort, url.getPath, url.getQuery, url.getRef).toURL

  def fileExists(fileName:String) ={
    def toBoolean(implicit unmarshaller:FromResponseUnmarshaller[FileInfo]):Future[HttpResponse] ⇒ Future[Boolean] = {
      (responseF:Future[HttpResponse]) => responseF.map{
        case res if res.status == StatusCodes.NotFound ⇒ false
        case res                                       ⇒ true
      }
    }
    val url = encodeUrl(new URL(s"${metaDataUrl.toString}$fileName"))
    val p = pipeline ~> toBoolean

    p(Get(url.toString))
  }

}

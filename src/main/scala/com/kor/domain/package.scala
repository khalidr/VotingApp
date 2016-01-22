package com.kor

import java.net.URL

import play.api.libs.functional.syntax._
import play.api.libs.json._
import reactivemongo.bson._

package object domain {


  sealed trait IncomingMessage
  case class TextMessage(payload:String) extends IncomingMessage
  case class MediaMessage(payload:URL) extends IncomingMessage
  case class VoiceMessage(payload:URL) extends IncomingMessage

  object IncomingMessage{
    implicit val reads: Reads[IncomingMessage] =
      for{
        r       <- (__ \"type").read[String]
        payload <- (__ \ "payload").read[String]
      } yield {
        r match {
          case "inboundText" => TextMessage(payload)
          case "inboundMedia" => MediaMessage(new URL(payload))
          case "voiceMail" => VoiceMessage(new URL(payload))
        }
      }
  }

  case class FileInfo(path:String, modified:String)
  object FileInfo{
    implicit val reads:Reads[FileInfo] =
      ((__ \ "size").read[String] ~
        (__ \ "path").read[String])(FileInfo.apply _)

  }

  case class MetaData(path:String, content:List[FileInfo])
  object MetaData{
    implicit val reads:Reads[MetaData] =
      ((__ \ "path").read[String] ~
        (__ \ "contents").read[List[FileInfo]])(MetaData.apply _)
  }


  case class Vote(picName:String, votes:Int)
  object Vote{
    implicit val formats:Format[Vote] = Json.format[Vote]
    implicit val handler = Macros.handler[Vote]
  }
}

package com.kor.repo

import com.kor.domain.Vote
import reactivemongo.api.MongoConnection
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ExecutionContext, Future}



trait Repository {

  def upsert(entity:Vote):Future[UpsertResult]
  def getByName(picName:String):Future[Option[Vote]]
  def getAll:Future[List[Vote]]

}

class VoteRepo(mongoConnection:MongoConnection, db:String)(implicit ec:ExecutionContext) extends Repository{

  private[this] val collection:BSONCollection = mongoConnection(db).collection("Votes")

  def upsert(entity: Vote) = collection.update(BSONDocument("picName" -> entity.picName), entity, upsert = true).map{wr => if(wr.ok) OK else Failed}

  def getByName(picName: String) = collection.find(BSONDocument("picName" -> picName)).one[Vote]

  def getAll = collection.find(BSONDocument()).cursor[Vote]().collect[List]()
}

sealed trait UpsertResult
case object OK extends UpsertResult
case object Failed extends UpsertResult



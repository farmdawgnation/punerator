package me.frmr.punerator
package model

import net.liftweb._
  import mongodb._
    import BsonDSL._

import java.util._

import org.bson.types._

case class Pun(
  author: String,
  pun: String,
  punny: Int = 0,
  tearable: Int = 0,
  createdAt: Date = new Date(),
  _id: ObjectId = ObjectId.get
) extends MongoDocument[Pun] {
  override val meta = Pun
}

object Pun extends MongoDocumentMeta[Pun] {
  override val formats = allFormats
}
